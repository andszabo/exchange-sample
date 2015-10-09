package com.github.andszabo.exchange.api

import java.time.ZonedDateTime

/**
 * Requests to place an [[Order]] with the specified attributes.
 */
case class PlaceOrder(id: String,
                      direction: Direction,
                      quantity: BigDecimal,
                      instrumentId: String,
                      price: BigDecimal,
                      user: String)
  extends InstrumentMessage with UserMessage {

  def createOrder(creationTime: ZonedDateTime) = Order(id, direction, quantity, instrumentId, price, user, creationTime)
}
