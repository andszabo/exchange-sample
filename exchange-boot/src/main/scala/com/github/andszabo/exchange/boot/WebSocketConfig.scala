package com.github.andszabo.exchange.boot

import java.util


import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.{AbstractWebSocketMessageBrokerConfigurer, EnableWebSocketMessageBroker, StompEndpointRegistry}

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

  override def configureMessageBroker(config: MessageBrokerRegistry): Unit = {
    config.enableSimpleBroker("/topic/", "/queue/")
    config.setApplicationDestinationPrefixes("/app")
  }


  def registerStompEndpoints(registry: StompEndpointRegistry): Unit = {
    registry.addEndpoint("/exchange").withSockJS()
  }

  override def configureMessageConverters(messageConverters: util.List[MessageConverter]): Boolean = {
    import com.github.andszabo.exchange.boot.JsonConfig._
    Array(
      PlaceOrderConverter,
      OrdersConverter,
      GetOrdersConverter,
      WatchInstrumentConverter,
      InstrumentInfoConverter,
      UserTradesConverter
    ).foreach(messageConverters add _)
    super.configureMessageConverters(messageConverters)
  }
}
