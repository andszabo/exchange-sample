package com.github.andszabo.exchange.api

/**
 * Collection of a user's orders.
 * Response to a [[GetOrders]].
 */
case class UserOrders(user: String, orders: List[Order]) extends UserMessage
