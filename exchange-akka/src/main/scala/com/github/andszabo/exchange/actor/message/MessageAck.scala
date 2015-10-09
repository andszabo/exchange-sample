package com.github.andszabo.exchange.actor.message

/**
 * Acknowledgement of the receipt of a [[GuaranteedMessage]].
 */
case class MessageAck(deliveryId: Long)
