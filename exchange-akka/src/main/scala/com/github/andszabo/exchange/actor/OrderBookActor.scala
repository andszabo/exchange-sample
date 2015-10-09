package com.github.andszabo.exchange.actor

import java.time.ZonedDateTime

import akka.actor.{ActorRef, Props}
import akka.persistence.{AtLeastOnceDelivery, PersistentActor}
import com.github.andszabo.exchange.actor.message.{GuaranteedMessage, MessageAck}
import com.github.andszabo.exchange.api._
import com.github.andszabo.exchange.domain._

object OrderBookActor {

  def props(exchange: ActorRef): Props = Props(new OrderBookActor(exchange))
}

class OrderBookActor(exchange: ActorRef) extends PersistentActor with AtLeastOnceDelivery {

  val orderBook = OrderBook(instrumentId)

  val createTrade = (order: Order, counterpart: Order) => {
    val (buyer, seller, buyOrderId, sellOrderId) = order.direction match {
      case Buy => (order.user, counterpart.user, order.id, counterpart.id)
      case Sell => (counterpart.user, order.user, counterpart.id, order.id)
    }
    new Trade(order.id, order.quantity, order.instrumentId, order.price, buyer, seller,
      buyOrderId, sellOrderId, order.creationTime)
  }

  override def persistenceId: String = s"Orders-${instrumentId}"

  def receiveRecover: Receive = {
    case order: Order => handleOrder(order)
    case tradeAck: MessageAck => confirmTrade(tradeAck)
  }

  def receiveCommand: Receive = {

    case cmd: PlaceOrder =>
      val order = cmd.createOrder(ZonedDateTime.now())
      persist(order) { o =>
        handleOrder(o)
        sender() ! UserOrders(o.user, List(o))
      }

    case tradeAck: MessageAck =>
      persist(tradeAck)(confirmTrade)

    case GetOrders(user, _) =>
      sender() ! UserOrders(user, orderBook.getOrdersByUser(user))
  }

  private def instrumentId: String = self.path.name

  private def handleOrder(order: Order): Unit = {
    for (counterpart <- orderBook.place(order)) {
      val trade = createTrade(order, counterpart)
      deliver(exchange.path)(deliveryId => GuaranteedMessage(deliveryId, trade))
    }
  }

  private def confirmTrade(tradeAck: MessageAck): Unit = confirmDelivery(tradeAck.deliveryId)
}
