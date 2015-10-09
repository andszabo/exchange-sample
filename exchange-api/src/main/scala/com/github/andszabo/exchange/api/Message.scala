package com.github.andszabo.exchange.api

/**
 * Marker for all API messages.
 */
trait Message extends Serializable

/**
 * Message that is related to a single user.
 */
trait UserMessage extends Message {

  def user: String
}

/**
 * Message that is related to a single instrument.
 */
trait InstrumentMessage extends Message {

  def instrumentId: String
}

