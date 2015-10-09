package com.github.andszabo.exchange.domain

import java.time.ZonedDateTime

import com.github.andszabo.exchange.api.Trade
import org.scalatest.{FlatSpec, Matchers}

class TradeStoreSpec extends FlatSpec with Matchers {

  "Empty TradeStore" should "return 0 for average execution price" in {
    val store = TradeStore("TEST")
    store.averageExecutionPrice should be(BigDecimal("0"))
  }

  "Non-empty TradeStore" should "correctly calculate the average execution price" in {
    val store = TradeStore("TEST")
    store add Trade("1", BigDecimal("100"), "TEST", BigDecimal("50"), "UserA", "UserB", "1", "2", ZonedDateTime.now())
    store.averageExecutionPrice should be(BigDecimal("50"))
    store add Trade("2", BigDecimal("200"), "TEST", BigDecimal("65"), "UserA", "UserB", "1", "2", ZonedDateTime.now())
    store.averageExecutionPrice should be(BigDecimal("60"))
  }

  it should "ignore a duplicate trade" in {
    val store = TradeStore("TEST")
    store add Trade("1", BigDecimal("100"), "TEST", BigDecimal("50"), "UserA", "UserB", "1", "2", ZonedDateTime.now())
    store add Trade("1", BigDecimal("100"), "TEST", BigDecimal("50"), "UserA", "UserB", "1", "2", ZonedDateTime.now())
    store.size should be(1)
  }

  it should "return trades for a specific user" in {
    def trade1 = Trade("1", BigDecimal("100"), "TEST", BigDecimal("50"), "UserA", "UserB", "1", "2", ZonedDateTime.now())
    def trade2 = Trade("2", BigDecimal("100"), "TEST", BigDecimal("50"), "UserC", "UserA", "1", "2", ZonedDateTime.now())
    val store = TradeStore("TEST")
    store add trade1
    store add trade2
    store.getTradesByUser("UserA") should contain theSameElementsAs (List(trade1, trade2))
    store.getTradesByUser("UserB") should contain theSameElementsAs (List(trade1))
    store.getTradesByUser("UserC") should contain theSameElementsAs (List(trade2))
    store.getTradesByUser("Other") should be(List.empty)
  }
}
