package com.github.andszabo.exchange.actor.message

import akka.cluster.sharding.ShardRegion.HashCodeMessageExtractor
import com.github.andszabo.exchange.api._

object HashByInstrumentId extends HashCodeMessageExtractor(30) {

  override def entityId(message: Any): String = message match {
    case msg: InstrumentMessage => msg.instrumentId
    case GuaranteedMessage(_, msg: InstrumentMessage) => msg.instrumentId
  }
}
