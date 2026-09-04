package com.mnp.mobilenumberportability.websocket;

import com.mnp.mobilenumberportability.config.CorsProperties;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final CorsProperties corsProperties;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Same allowed-origins list as the REST API (mnp.cors.allowed-origins) —
        // see WebConfig for why this can no longer be a single hardcoded origin.
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(corsProperties.allowedOrigins().toArray(new String[0]));
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // A simple in-memory broker is plenty for a broadcast-only, single-instance
        // app no client ever sends messages back through /app, only subscribes.
        registry.enableSimpleBroker("/topic");
    }
}
