package com.github.andszabo.exchange.api

/**
 * Collection of a user's trades.
 * Response to a [[GetTrades]].
 */
case class UserTrades(user: String, trades: List[Trade]) extends UserMessage
