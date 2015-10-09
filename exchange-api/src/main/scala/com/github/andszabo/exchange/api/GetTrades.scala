package com.github.andszabo.exchange.api

/**
 * Requests a user's trades in an instrument.
 * Responded by a [[UserTrades]].
 */
case class GetTrades(user: String, instrumentId: String) extends InstrumentMessage with UserMessage
