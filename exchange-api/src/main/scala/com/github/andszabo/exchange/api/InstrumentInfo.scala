package com.github.andszabo.exchange.api

import java.time.ZonedDateTime

/**
 * Market information about an instrument.
 * Response to a [[GetInstrumentInfo]].
 * @param instrumentId the instrument.
 * @param averagePrice average execution price of trades in this instrument.
 * @param timestamp time when this information was looked up.
 */
case class InstrumentInfo(instrumentId: String, averagePrice: BigDecimal, timestamp: ZonedDateTime) extends InstrumentMessage
