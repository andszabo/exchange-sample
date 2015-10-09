package com.github.andszabo.exchange.api

/**
 * Requests a user's orders in a specific instrument.
 * Responded by a [[UserOrders]].
 */
case class GetOrders(user: String, instrumentId: String) extends InstrumentMessage with UserMessage
