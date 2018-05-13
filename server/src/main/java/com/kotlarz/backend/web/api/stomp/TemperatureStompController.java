package com.kotlarz.backend.web.api.stomp;

import com.kotlarz.backend.service.LastTemperaturesResolver;
import com.kotlarz.backend.service.TemperatureService;
import com.kotlarz.backend.web.dto.NewTemperatureDto;
import com.kotlarz.backend.web.live.LiveTemperaturesPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
public class TemperatureStompController {
    @Autowired
    private TemperatureService temperatureService;

    @Autowired
    private LastTemperaturesResolver lastTemperaturesResolver;

    @Autowired
    private LiveTemperaturesPublisher liveTemperaturesPublisher;

    @MessageMapping("/temperatures")
    public void reportNew(List<NewTemperatureDto> temperatureDtos) {
        temperatureService.report(temperatureDtos);

        lastTemperaturesResolver.report(temperatureDtos);
        liveTemperaturesPublisher.send();
    }
}
