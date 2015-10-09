package com.github.andszabo.exchange.domain

import com.github.andszabo.exchange.api.Trade

import scala.collection.{immutable, mutable}
import scala.math.BigDecimal.RoundingMode

object TradeStore {

  def apply(instrumentId: String) = new TradeStore(instrumentId)
}

class TradeStore(instrumentId: String) {

  private val trades = mutable.LinkedHashSet[Trade]()

  def add(trade: Trade): Boolean = trades add trade

  def size: Int = trades.size

  def averageExecutionPrice: BigDecimal = {
    trades.view.
      map(t => (t.price * t.quantity, t.quantity)).
      reduceOption((t1, t2) => (t1._1 + t2._1, t1._2 + t2._2)).
      map(t => (t._1 / t._2).setScale(4,RoundingMode.HALF_UP)).
      getOrElse(BigDecimal("0"))
  }

  def getTradesByUser(user: String): immutable.List[Trade] =
    trades.filter(t => t.buyer == user || t.seller == user).toList

}
