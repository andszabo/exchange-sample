package com.github.andszabo.exchange.domain

import java.time.ZonedDateTime

import com.github.andszabo.exchange.api.{Buy, Direction, Order}

import scala.collection.{Traversable, mutable}

object OrderBook {

  private val dateTimeOrdering: Ordering[ZonedDateTime] = Ordering.fromLessThan((x, y) => x.isAfter(y))

  private val buyOrdering: Ordering[Order] = Ordering.by((o: Order) => (o.price, o.creationTime, o.id))(
    Ordering.Tuple3(Ordering.BigDecimal, dateTimeOrdering, Ordering.String))

  private val sellOrdering: Ordering[Order] = Ordering.by((o: Order) => (o.price, o.creationTime, o.id))(
    Ordering.Tuple3(Ordering.BigDecimal.reverse, dateTimeOrdering, Ordering.String))

  def apply(instrumentId: String) = new OrderBook(instrumentId)

  def apply(instrumentId: String, initialOrders: Traversable[Order]) = {
    val orderBook = new OrderBook(instrumentId)
    initialOrders foreach (order => orderBook.place(order))
    orderBook
  }
}

class OrderBook(instrumentId: String) {

  import OrderBook._

  private val orders: mutable.HashMap[BigDecimal, mutable.TreeSet[Order]] = mutable.HashMap.empty

  def place(order: Order): Option[Order] = {
    val counterpart = removeMatching(order)
    if (counterpart.isEmpty) {
      add(order)
    }
    counterpart
  }

  def size: Int = orders.values map (_.size) sum

  def getOrdersByUser(user: String): List[Order] = orders.values.flatten.filter(_.user == user).toList

  private def add(order: Order): Unit = getOrdersBy(order.quantity, order.direction) += order

  private def removeMatching(order: Order): Option[Order] = {
    val potentialMatches = getOrdersBy(order.quantity, order.direction.reverse())
    if (potentialMatches.iteratorFrom(order).hasNext) {
      val counterpart = potentialMatches.last
      potentialMatches -= counterpart
      Option(counterpart)
    } else
      Option.empty
  }

  private def getOrdersBy(quantity: BigDecimal, direction: Direction): mutable.TreeSet[Order] = {
    val adjustedQuantity = if (direction == Buy) quantity else -quantity
    val ordering = if (direction == Buy) buyOrdering else sellOrdering
    orders.getOrElseUpdate(adjustedQuantity, mutable.TreeSet.empty(ordering))
  }
}


