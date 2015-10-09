package com.github.andszabo.exchange.actor

import akka.actor.{Actor, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import com.github.andszabo.exchange.actor.message.{GuaranteedMessage, HashByInstrumentId}
import com.github.andszabo.exchange.api._

object ExchangeActor {

  def props(): Props = Props(new ExchangeActor())
}

class ExchangeActor extends Actor {

  val settings = ClusterShardingSettings(context.system)

  val orderBookRegion = ClusterSharding.get(context.system).start("orders",
    OrderBookActor.props(self), settings, HashByInstrumentId)

  val tradeStoreRegion = ClusterSharding.get(context.system).start("trades",
    TradeStoreActor.props(), settings, HashByInstrumentId)

  override def receive: Receive = {
    case msg: PlaceOrder => orderBookRegion forward msg
    case msg: GetOrders => orderBookRegion forward msg
    case msg @ GuaranteedMessage(_, trade: Trade) => tradeStoreRegion forward msg
    case msg: GetTrades => tradeStoreRegion forward msg
    case msg: GetInstrumentInfo => tradeStoreRegion forward msg
  }
}
