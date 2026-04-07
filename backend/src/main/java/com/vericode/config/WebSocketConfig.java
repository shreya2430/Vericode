package com.vericode.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Bridge Pattern + Observer Pattern: STOMP WebSocket configuration.
 *
 * Registers the /ws endpoint that the frontend connects to, and configures
 * a simple in-memory message broker on /topic so WebSocketChannel can
 * broadcast PR status changes to all connected clients.
 *
 * The frontend subscribes to /topic/pr-updates and receives a JSON payload
 * whenever any PR status changes, regardless of who the author is.
 * This is the key difference from SSE (which only notifies the PR author):
 * WebSocket notifies every connected user, enabling live PR list updates.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // In-memory broker handling subscriptions to /topic/**
        registry.enableSimpleBroker("/topic");
        // Prefix for messages sent from client to server (not used yet, but standard to define)
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The endpoint the frontend connects to: new SockJS('/ws') or new WebSocket('/ws')
        // withSockJS() provides fallback for browsers that don't support native WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000");
    }
}
