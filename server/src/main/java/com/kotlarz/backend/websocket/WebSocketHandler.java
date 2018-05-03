package com.kotlarz.backend.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kotlarz.backend.web.dto.NewTemperatureDto;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.LinkedList;
import java.util.List;

@Service
public class WebSocketHandler extends TextWebSocketHandler {
    private List<WebSocketSession> sessions = new LinkedList<>();

    private ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public void sendNewLogs(List<NewTemperatureDto> temperatureDtos) {
        synchronized (sessions) {
            String json = mapper.writeValueAsString(temperatureDtos);

            for (WebSocketSession session : sessions) {
                session.sendMessage(new TextMessage(json));
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        synchronized (sessions) {
            sessions.add(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        synchronized (sessions) {
            sessions.removeIf(foundSession -> foundSession.getId().equals(session.getId()));
        }
    }
}
