package com.github.andszabo.exchange.api

import java.time.ZonedDateTime

/**
 * Trade created by matching two orders of the same instrument and quantity and opposite direction.
 * @param id the trade id.
 * @param quantity the traded quantity.
 * @param instrumentId the instrument.
 * @param price the price at which the trade will take place.
 * @param buyer the buyer.
 * @param seller the seller.
 * @param buyOrderId the id of the buy order this trade is created from.
 * @param sellOrderId the id of the sell order this trade is created from.
 * @param creationTime the time the trade was created.
 */
case class Trade(
                  id: String,
                  quantity: BigDecimal,
                  instrumentId: String,
                  price: BigDecimal,
                  buyer: String,
                  seller: String,
                  buyOrderId: String,
                  sellOrderId: String,
                  creationTime: ZonedDateTime) extends InstrumentMessage {

  override def hashCode(): Int = if (id eq null) id.hashCode else 1

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: Trade => this.id == that.id
    case _ => false
  }
}
