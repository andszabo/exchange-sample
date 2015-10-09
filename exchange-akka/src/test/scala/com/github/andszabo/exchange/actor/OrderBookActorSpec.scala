package com.github.andszabo.exchange.actor

import java.time.ZonedDateTime

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.github.andszabo.exchange.actor.message.GuaranteedMessage
import com.github.andszabo.exchange.api._
import org.scalatest._

class OrderBookActorSpec(_system: ActorSystem)
  extends TestKit(_system) with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

  var orderBook: ActorRef = null
  var seq = 1L
  var instrumentId = "TEST"

  def this() = this(ActorSystem("OrderBookActorSpec"))

  override def beforeEach = {
    instrumentId = s"TEST${seq}"
    seq += 1
    orderBook = system.actorOf(OrderBookActor.props(testActor), instrumentId)
  }

  override def afterEach = system stop orderBook

  override def afterAll = TestKit.shutdownActorSystem(system)


  "An OrderBookActor" should "accept and order with the right instrument" in {
    orderBook ! PlaceOrder("1", Buy, BigDecimal("1000"), instrumentId, BigDecimal("20"), "userA")
    expectMsgPF() {
      case msg: UserOrders =>
        assert(msg.user == "userA")
        assert(msg.orders.size == 1)
        assert(msg.orders(0).id == "1")
    }
  }

  it should "publish a trade when a match is found" in {

    orderBook ! Order("1", Buy, BigDecimal("1000"), instrumentId, BigDecimal("20"), "userA", ZonedDateTime.now())
    orderBook ! Order("2", Sell, BigDecimal("1000"), instrumentId, BigDecimal("19"), "userB", ZonedDateTime.now())
    receiveWhile(messages = 3) {
      case msg: UserOrders if Set("1", "2").contains(msg.orders(0).id) => "OK"
      case GuaranteedMessage(_, trade: Trade) =>
        assert(trade.id == "2")
        assert(trade.quantity == BigDecimal("1000"))
        assert(trade.instrumentId == instrumentId)
        assert(trade.price == BigDecimal("19"))
        assert(trade.buyer == "userA")
        assert(trade.seller == "userB")
    }
  }
}
