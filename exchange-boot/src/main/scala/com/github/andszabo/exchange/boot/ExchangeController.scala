package com.github.andszabo.exchange.boot

import java.security.Principal
import javax.annotation.Resource

import akka.actor.{InvalidActorNameException, ActorRef, ActorSystem}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import com.github.andszabo.exchange.actor._
import com.github.andszabo.exchange.api._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.messaging.handler.annotation.{MessageExceptionHandler, MessageMapping}
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller

@Controller
class ExchangeController @Autowired()(@Resource() exchange: ActorRef,
                                      @Resource() actorSystem: ActorSystem,
                                      @Resource() msgTemplate: SimpMessagingTemplate)
  extends UserNotifier with ApplicationListener[AuthenticationSuccessEvent] {


  override def onApplicationEvent(event: AuthenticationSuccessEvent): Unit = {
    val userDetails = event.getAuthentication.getPrincipal.asInstanceOf[UserDetails]
    try {
      actorSystem.actorOf(UserActor.props(exchange, this), s"User-${userDetails.getUsername}")
    } catch {
      case e: InvalidActorNameException => // Actor already exists
    }
  }

  @MessageMapping(Array("/exchange/order"))
  def placeOrder(cmd: PlaceOrder, principal: Principal): Unit = {
    checkUser(cmd.user, principal)
    sendToUserActor(cmd, principal)
  }

  @MessageMapping(Array("/exchange/instrument"))
  def watchInstrument(cmd: WatchInstrument, principal: Principal): Unit = {
    checkUser(cmd.user, principal)
    sendToUserActor(cmd, principal)
  }

  @MessageMapping(Array("/exchange/trades"))
  def getTrades(cmd: GetTrades, principal: Principal): Unit = {
    checkUser(cmd.user, principal)
    sendToUserActor(cmd, principal)
  }

  @MessageExceptionHandler
  @SendToUser(value = Array("/queue/errors"), broadcast = false)
  def handleException(exception: Exception): String = exception.getMessage


  private def sendToUserActor(cmd: Message, principal: Principal): Unit = {
    actorSystem.actorSelection(s"/user/User-${principal.getName}") ! cmd
  }

  override def send(user: String, msg: UserTrades): Unit = {
    msgTemplate.convertAndSendToUser(user, "/queue/trade", msg)
  }

  override def send(user: String, msg: InstrumentInfo): Unit = {
    msgTemplate.convertAndSendToUser(user, "/queue/instrument", msg)
  }

  override def send(user: String, msg: UserOrders): Unit = {
    msgTemplate.convertAndSendToUser(user, "/queue/order", msg)
  }

  private def checkUser(user: String, principal: Principal): Unit = {
    if (user != principal.getName) {
      throw new IllegalArgumentException("Command's user must match the logged in user.")
    }
  }
}
