package com.kotlarz.backend.web.live.stomp;

import com.kotlarz.backend.service.LastTemperaturesResolver;
import com.kotlarz.backend.web.live.LiveTemperaturesPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
public class OnSubscribeLiveTemperaturesEvent implements ApplicationListener<SessionSubscribeEvent> {
    @Autowired
    private LiveTemperaturesPublisher liveTemperaturesPublisher;

    @Autowired
    private LastTemperaturesResolver lastTemperaturesResolver;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info(headerAccessor.toString());

        if (headerAccessor.getDestination().equals(LiveTemperaturesPublisher.DESTINATION)) {
            liveTemperaturesPublisher.sendByStompToUser(lastTemperaturesResolver.getLastCached(), event.getUser().getName());
        }
    }
}
