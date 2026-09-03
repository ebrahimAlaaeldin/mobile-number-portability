package com.mnp.mobilenumberportability.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

/**
 * STOMP-over-WebSocket transport for instant porting-request updates. Clients
 * connect to {@code /ws} and subscribe to {@code /topic/porting-requests};
 * {@link PortingRequestNotifier} is the only thing that ever publishes to it.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // A simple in-memory broker is plenty for a broadcast-only, single-instance
        // app no client ever sends messages back through /app, only subscribes.
        registry.enableSimpleBroker("/topic");
    }
}
