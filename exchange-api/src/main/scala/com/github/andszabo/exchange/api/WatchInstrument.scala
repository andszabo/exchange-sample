package com.github.andszabo.exchange.api

/**
 * Request by a user to watch an instrument.
 * Responded by the user's [[UserOrders]] and [[UserTrades]] in that instrument and by periodic
 * [[InstrumentInfo]] updates for the instrument.
 */
case class WatchInstrument(user: String, instrumentId: String) extends InstrumentMessage with UserMessage
