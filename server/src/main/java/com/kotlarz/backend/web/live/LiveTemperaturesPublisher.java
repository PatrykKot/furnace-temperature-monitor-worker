package com.kotlarz.backend.web.live;

import com.kotlarz.backend.service.LastTemperaturesResolver;
import com.kotlarz.backend.web.dto.NewTemperatureDto;
import com.kotlarz.backend.web.live.websocket.WebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LiveTemperaturesPublisher {
    public static final String DESTINATION = "/topic/temperatures/live";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private LastTemperaturesResolver lastTemperaturesResolver;

    @Autowired
    private WebSocketHandler webSocketHandler;

    public void send() {
        List<NewTemperatureDto> logs = lastTemperaturesResolver.getLastCached();
        if (!logs.isEmpty()) {
            sendByStomp(logs);
            sendByWebSocket(logs);
        } else {
            log.info("No live logs to send");
        }
    }

    public void sendByStomp(List<NewTemperatureDto> logs) {
        log.info("Sending " + logs.size() + " logs to stomp clients");
        messagingTemplate.convertAndSend(DESTINATION, logs);
    }

    public void sendByStompToUser(List<NewTemperatureDto> logs, String username) {
        messagingTemplate.convertAndSendToUser(username, DESTINATION, logs);
    }

    public void sendByWebSocket(List<NewTemperatureDto> logs) {
        log.info("Sending " + logs.size() + " logs to websocket clients");
        webSocketHandler.sendNewLogs(logs);
    }
}
