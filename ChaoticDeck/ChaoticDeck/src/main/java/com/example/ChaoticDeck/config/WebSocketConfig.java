package com.example.ChaoticDeck.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // ใช้ /topic สำหรับส่งข้อความกลับไปหา Client (เช่น /topic/room/{roomId})
        config.enableSimpleBroker("/topic");
        // ใช้ /app สำหรับ Client ส่งข้อมูลมาที่ Server (เช่น /app/playCard)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint ที่ Client ใช้เชื่อมต่อเข้ามา
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
