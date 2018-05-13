package com.kotlarz.backend.web.api.rest;

import com.kotlarz.backend.service.LastTemperaturesResolver;
import com.kotlarz.backend.service.TemperatureService;
import com.kotlarz.backend.web.dto.NewTemperatureDto;
import com.kotlarz.backend.web.dto.SensorWithLogsDto;
import com.kotlarz.backend.web.live.LiveTemperaturesPublisher;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("temperatures")
public class TemperatureController {
    @Autowired
    private TemperatureService temperatureService;

    @Autowired
    private LastTemperaturesResolver lastTemperaturesResolver;

    @Autowired
    private LiveTemperaturesPublisher liveTemperaturesPublisher;

    @GetMapping("sensors")
    public List<SensorWithLogsDto> getAll() {
        return temperatureService.getForAllSensors().stream()
                .map(sensor -> SensorWithLogsDto.create(sensor))
                .collect(Collectors.toList());
    }

    @GetMapping("sensors/later/{date}")
    public List<SensorWithLogsDto> findLaterThan(@PathVariable("date") Long date) {
        return temperatureService.findLaterThan(new Date(date));
    }

    @PostMapping
    public void reportNew(@RequestBody List<NewTemperatureDto> temperatureDtos) {
        temperatureService.report(temperatureDtos);

        lastTemperaturesResolver.report(temperatureDtos);
        liveTemperaturesPublisher.send();
    }
}
