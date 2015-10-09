package com.github.andszabo.exchange.actor.message

import com.github.andszabo.exchange.api.Message

/**
 * A message envelope with guaranteed at-least-once delivery.
 */
case class GuaranteedMessage(deliveryId: Long, msg: Message)
