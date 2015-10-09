package com.github.andszabo.exchange.boot

import java.time._
import java.time.format.DateTimeFormatter

import com.github.andszabo.exchange.api._
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.support.MessageBuilder
import org.springframework.messaging.{Message, MessageHeaders}
import spray.json._

/**
 *
 */
object JsonConfig extends DefaultJsonProtocol {

  implicit object DirectionJsonFormat extends RootJsonFormat[Direction] {

    def write(obj: Direction) = JsString(obj.toString)

    def read(value: JsValue) = value match {
      case JsString("Buy") => Buy
      case JsString("Sell") => Sell
      case _ => throw new DeserializationException("Direction expected")
    }
  }

  implicit object ZonedDateTimeFormat extends RootJsonFormat[ZonedDateTime] {

    def write(obj: ZonedDateTime): JsValue = JsString(obj.format(DateTimeFormatter.ISO_INSTANT))

    def read(value: JsValue): ZonedDateTime = value match {
      case JsString(s) => ZonedDateTime.parse(s, DateTimeFormatter.ISO_INSTANT)
      case _ => throw new DeserializationException("ZonedDateTime expected")
    }
  }

  implicit val placeOrderFormat = jsonFormat6(PlaceOrder.apply)
  implicit val orderFormat = jsonFormat7(Order.apply)
  implicit val userOrdersFormat = jsonFormat2(UserOrders.apply)
  implicit val getOrdersFormat = jsonFormat2(GetOrders.apply)
  implicit val watchInstrumentFormat = jsonFormat2(WatchInstrument.apply)
  implicit val instrumentInfoFormat = jsonFormat3(InstrumentInfo.apply)
  implicit val tradeFormat = jsonFormat9(Trade.apply)
  implicit val userTradesFormat = jsonFormat2(UserTrades.apply)

  object PlaceOrderConverter extends GenericMessageConverter[PlaceOrder](classOf[PlaceOrder]) {

    override def toJson(payload: PlaceOrder): JsValue = payload.toJson

    override def fromJson(json: JsValue): PlaceOrder = json.convertTo[PlaceOrder]
  }

  object GetOrdersConverter extends GenericMessageConverter[GetOrders](classOf[GetOrders]) {

    override def toJson(payload: GetOrders): JsValue = payload.toJson

    override def fromJson(json: JsValue): GetOrders = json.convertTo[GetOrders]
  }

  object OrdersConverter extends GenericMessageConverter[UserOrders](classOf[UserOrders]) {

    override def toJson(payload: UserOrders): JsValue = payload.toJson

    override def fromJson(json: JsValue): UserOrders = json.convertTo[UserOrders]
  }

  object WatchInstrumentConverter extends GenericMessageConverter[WatchInstrument](classOf[WatchInstrument]) {

    override def toJson(payload: WatchInstrument): JsValue = payload.toJson

    override def fromJson(json: JsValue): WatchInstrument = json.convertTo[WatchInstrument]
  }

  object InstrumentInfoConverter extends GenericMessageConverter[InstrumentInfo](classOf[InstrumentInfo]) {

    override def toJson(payload: InstrumentInfo): JsValue = payload.toJson

    override def fromJson(json: JsValue): InstrumentInfo = json.convertTo[InstrumentInfo]
  }

  object UserTradesConverter extends GenericMessageConverter[UserTrades](classOf[UserTrades]) {

    override def toJson(payload: UserTrades): JsValue = payload.toJson

    override def fromJson(json: JsValue): UserTrades = json.convertTo[UserTrades]
  }
}

abstract class GenericMessageConverter[T <: AnyRef](clazz: Class[T]) extends MessageConverter {

  import spray.json._

  def toJson(payload: T): JsValue

  def fromJson(json: JsValue): T

  override def toMessage(payload: scala.Any, header: MessageHeaders): Message[_] = {
    if (clazz isInstance payload)
      MessageBuilder.createMessage(toJson(payload.asInstanceOf[T]).compactPrint.getBytes("UTF-8"), header)
    else
      null
  }

  override def fromMessage(message: Message[_], targetClass: Class[_]): AnyRef = {
    if (clazz equals targetClass)
      fromJson(new String(message.getPayload.asInstanceOf[Array[Byte]], "UTF-8").parseJson)
    else
      null
  }
}


