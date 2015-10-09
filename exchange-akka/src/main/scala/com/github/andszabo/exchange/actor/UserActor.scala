package com.github.andszabo.exchange.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}
import com.github.andszabo.exchange.api._

import scala.concurrent.duration._

trait UserNotifier {

  def send(user: String, msg: UserTrades): Unit

  def send(user: String, msg: InstrumentInfo): Unit

  def send(user: String, msg: UserOrders): Unit
}

object UserActor {

  val instrumentInfoFrequency = 30 seconds

  def props(exchange: ActorRef, userNotifier: UserNotifier): Props = Props(new UserActor(exchange, userNotifier))
}

class UserActor(exchange: ActorRef, userNotifier: UserNotifier) extends Actor with ActorLogging {

  import context._

  val mediator = DistributedPubSub(context.system).mediator

  val user: String = self.path.name.substring("User-".length)

  mediator ! Subscribe(user, self)

  var watchedInstrument: Option[String] = Option.empty

  override def receive: Receive = {
    case msg: PlaceOrder => exchange ! msg
    case msg: GetOrders => exchange ! msg
    case msg: UserOrders if msg.orders.nonEmpty => userNotifier.send(user, msg)
    case msg: GetTrades => exchange ! msg
    case msg: UserTrades if msg.trades.nonEmpty => userNotifier.send(user, msg)
    case WatchInstrument(user, instrumentId) => watchInstrument(instrumentId)
    case msg: GetInstrumentInfo if watchedInstrument.contains(msg.instrumentId) => sendAndReschedule(msg)
    case msg: InstrumentInfo if watchedInstrument.contains(msg.instrumentId) => userNotifier.send(user, msg)
    case trade: Trade => userNotifier.send(user, UserTrades(user, List(trade)))
    case SubscribeAck(Subscribe(user, _, _)) => log.debug("Actor User-{} successfully subscribed for responses.", user)
  }

  private def watchInstrument(instrumentId: String): Unit = {
    watchedInstrument = Option(instrumentId)
    exchange ! GetOrders(user, instrumentId)
    exchange ! GetTrades(user, instrumentId)
    sendAndReschedule(GetInstrumentInfo(instrumentId))
  }

  private def sendAndReschedule(msg: GetInstrumentInfo): Unit = {
    exchange ! msg
    context.system.scheduler.scheduleOnce(UserActor.instrumentInfoFrequency, self, msg)
  }

}
