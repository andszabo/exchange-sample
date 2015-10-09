package com.github.andszabo.exchange.api

import java.time.ZonedDateTime

/**
 * Order to buy or sell given quantity of an instrument at the specified price or better.
 * @param id unique id of the order.
 * @param direction Buy/Sell.
 * @param quantity the quantity.
 * @param instrumentId the instrument.
 * @param price the price.
 * @param user the user who placed this order.
 * @param creationTime the time the order was recorded in the order book.
 */
case class Order(
                  id: String,
                  direction: Direction,
                  quantity: BigDecimal,
                  instrumentId: String,
                  price: BigDecimal,
                  user: String,
                  creationTime: ZonedDateTime)
  extends InstrumentMessage with UserMessage {

  override def hashCode(): Int = if (id eq null) id.hashCode else 1

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: Order => this.id == that.id
    case _ => false
  }
}
