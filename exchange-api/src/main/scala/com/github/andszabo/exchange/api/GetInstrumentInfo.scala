package com.github.andszabo.exchange.api

/**
 * Requests information about an instrument.
 * Responded by an [[InstrumentInfo]].
 */
case class GetInstrumentInfo(instrumentId: String) extends InstrumentMessage
