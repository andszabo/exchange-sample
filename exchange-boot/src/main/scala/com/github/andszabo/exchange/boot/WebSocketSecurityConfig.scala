package com.github.andszabo.exchange.boot

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.SimpMessageType.{MESSAGE, SUBSCRIBE}
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer

@Configuration
class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

  override protected def configureInbound(messages: MessageSecurityMetadataSourceRegistry) = {
    messages

		// message types other than MESSAGE and SUBSCRIBE
		.nullDestMatcher().authenticated()
				// anyone can subscribe to the errors
				.simpSubscribeDestMatchers("/user/queue/errors").permitAll()
				// matches any destination that starts with /app/
				.simpDestMatchers("/app/**").authenticated()
				// matches any destination for SimpMessageType.SUBSCRIBE that starts with /user/
				.simpSubscribeDestMatchers("/user/**").authenticated()

				// (i.e. cannot send messages directly to /topic/, /queue/)
				// (i.e. cannot subscribe to /topic/messages/* to get messages sent to
				// /topic/messages-user<id>)
				.simpTypeMatchers(MESSAGE, SUBSCRIBE).denyAll()
				// catch all
				.anyMessage().denyAll()
  }

}
