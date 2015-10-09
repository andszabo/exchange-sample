package com.github.andszabo.exchange.actor

import java.time.ZonedDateTime

import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import akka.persistence.PersistentActor
import com.github.andszabo.exchange.actor.message.{GuaranteedMessage, MessageAck}
import com.github.andszabo.exchange.api._
import com.github.andszabo.exchange.domain.TradeStore

object TradeStoreActor {

  def props(): Props = Props(new TradeStoreActor())
}

class TradeStoreActor() extends PersistentActor {

  val mediator = DistributedPubSub(context.system).mediator

  val store = TradeStore(instrumentId)

  override def persistenceId: String = s"Trades-${instrumentId}"

  override def receiveRecover: Receive = {
    case trade: Trade => store.add(trade)
  }

  override def receiveCommand: Receive = {
    case GuaranteedMessage(deliveryId, trade: Trade) =>
      persist(trade) { trade =>
        store.add(trade)
        sender() ! MessageAck(deliveryId)
        mediator ! Publish(trade.buyer, trade)
        mediator ! Publish(trade.seller, trade)
      }

    case GetInstrumentInfo(instrumentId) =>
      sender() ! InstrumentInfo(instrumentId, store.averageExecutionPrice, ZonedDateTime.now())

    case GetTrades(user, _) =>
      sender() ! UserTrades(user, store.getTradesByUser(user))
  }

  private def instrumentId: String = self.path.name

}
