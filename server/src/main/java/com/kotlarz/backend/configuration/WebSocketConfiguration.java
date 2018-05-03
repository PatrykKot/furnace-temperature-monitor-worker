package com.kotlarz.backend.configuration;

import com.kotlarz.backend.websocket.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.concurrent.Executors;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    @Autowired
    private WebSocketHandler handler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/websocket").setAllowedOrigins("*");
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());
    }
}
