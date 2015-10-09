package com.github.andszabo.exchange.domain

import java.time._

import com.github.andszabo.exchange.api.{Buy, Direction, Order, Sell}
import org.scalatest._

class OrderBookSpec extends FlatSpec with Matchers {

  var seq = 0L
  val start = Clock.fixed(Instant.now(), ZoneId.systemDefault())

  "An empty OrderBook" should "store a new order" in {
    val orderBook = OrderBook("VOD.L")
    orderBook.place(Order(Buy, "1000", "VOD.L", "100.2", "UserA"))
    orderBook.size should be(1)
  }

  "A non-empty OrderBook" should "return none if there is no matching buy for a sell" in {
    val orderBook = OrderBook("VOD.L", List(
      Order(Buy, "1000", "VOD.L", "100.2", "UserA"),
      Order(Buy, "1200", "VOD.L", "102.7", "UserA"),
      Order(Sell, "1000", "VOD.L", "105.0", "UserA")
    ))
    val sellOrder = Order(Sell, "1000", "VOD.L", "101.5", "UserB")
    orderBook.place(sellOrder) should be(Option.empty)
    orderBook.size should be(4)
  }

  "A non-empty OrderBook" should "return none if there is no matching sell for a buy" in {
    val orderBook = OrderBook("VOD.L", List(
      Order(Sell, "1000", "VOD.L", "102.7", "UserA"),
      Order(Sell, "1200", "VOD.L", "100.2", "UserA"),
      Order(Buy, "1000", "VOD.L", "100.0", "UserA")
    ))
    val sellOrder = Order(Buy, "1000", "VOD.L", "101.5", "UserB")
    orderBook.place(sellOrder) should be(Option.empty)
    orderBook.size should be(4)
  }

  it should "return matching order" in {
    val buyOrder = Order(Buy, "1000", "VOD.L", "100.2", "UserA")
    val orderBook = OrderBook("VOD.L", List(buyOrder))
    val sellOrder = Order(Sell, "1000", "VOD.L", "100.1", "UserB")
    orderBook.place(sellOrder) should be(Option(buyOrder))
  }

  it should "match a sell order to the buy with the highest price" in {
    val buy1 = Order(Buy, "1000", "VOD.L", "100.2", "UserA")
    val buy2 = Order(Buy, "1000", "VOD.L", "102.7", "UserA")
    val orderBook = OrderBook("VOD.L", List(buy1, buy2))
    val sellOrder = Order(Sell, "1000", "VOD.L", "100", "UserB")
    orderBook.place(sellOrder) should be(Option(buy2))
  }

  it should "match a buy order to the sell with the lowest price" in {
    val sell1 = Order(Sell, "1000", "VOD.L", "100.2", "UserA")
    val sell2 = Order(Sell, "1000", "VOD.L", "102.7", "UserA")
    val orderBook = OrderBook("VOD.L", List(sell1, sell2))
    val buyOrder = Order(Buy, "1000", "VOD.L", "105", "UserB")
    orderBook.place(buyOrder) should be(Option(sell1))
  }

  it should "match a sell order to the earliest buy among multiple buys at the same price" in {
    val buy1 = Order(Buy, "1000", "VOD.L", "100.2", "UserA")
    val buy2 = Order(Buy, "1000", "VOD.L", "102.7", "UserA")
    val buy3 = Order(Buy, "1000", "VOD.L", "102.7", "UserA")
    val orderBook = OrderBook("VOD.L", List(buy1, buy2, buy3))
    val sellOrder = Order(Sell, "1000", "VOD.L", "100", "UserB")
    orderBook.place(sellOrder) should be(Option(buy2))
  }

  it should "match a buy order to the earliest sell among multiple sells at the same price" in {
    val sell1 = Order(Sell, "1000", "VOD.L", "102.7", "UserA")
    val sell2 = Order(Sell, "1000", "VOD.L", "100.2", "UserA")
    val sell3 = Order(Sell, "1000", "VOD.L", "100.2", "UserA")
    val orderBook = OrderBook("VOD.L", List(sell1, sell2, sell3))
    val buyOrder = Order(Buy, "1000", "VOD.L", "105", "UserB")
    orderBook.place(buyOrder) should be(Option(sell2))
  }

  it should "return orders for a specific user" in {
    val buyA = Order(Buy, "700", "VOD.L", "97.2", "UserA")
    val buyB = Order(Buy, "1000", "VOD.L", "99.1", "UserB")
    val sellA = Order(Sell, "200", "VOD.L", "102.7", "UserA")
    val orderBook = OrderBook("VOD.L", List(buyA, buyB, sellA))
    orderBook.getOrdersByUser("UserA") should contain theSameElementsAs (List(buyA, sellA))
    orderBook.getOrdersByUser("UserB") should contain theSameElementsAs  (List(buyB))
    orderBook.getOrdersByUser("Other") should be (List.empty)
  }


  def Order(direction: Direction, quantity: String, instrumentId: String, price: String, user: String): Order = {
    seq += 1
    val creationTime: ZonedDateTime = ZonedDateTime.now(Clock.offset(start, Duration.ofMillis(seq)))
    new Order("" + seq, direction, BigDecimal(quantity), instrumentId, BigDecimal(price), user, creationTime)
  }
}
