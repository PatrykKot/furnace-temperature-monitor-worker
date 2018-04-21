package com.kotlarz.backend.service;

import com.kotlarz.backend.domain.Sensor;
import com.kotlarz.backend.domain.TemperatureLog;
import com.kotlarz.backend.persistance.SensorRepository;
import com.kotlarz.backend.persistance.TemperatureLogRepository;
import com.kotlarz.backend.web.dto.NewTemperatureDto;
import com.kotlarz.backend.web.dto.SensorWithLogsDto;
import com.kotlarz.backend.web.dto.TemperatureLogDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TemperatureService {
    @Autowired
    private TemperatureLogRepository temperatureLogRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Transactional
    public void report(List<NewTemperatureDto> temperatureDtos) {
        List<Sensor> sensors = temperatureDtos.stream()
                .map(dto -> dto.getAddress())
                .distinct()
                .map(address -> sensorRepository.findByAddress(address).orElseGet(() -> Sensor.builder()
                        .address(address)
                        .build()))
                .collect(Collectors.toList());

        List<TemperatureLog> logs = temperatureDtos.stream()
                .map(dto -> TemperatureLog.builder()
                        .date(dto.getDate())
                        .sensor(sensors.stream()
                                .filter(sensor -> sensor.getAddress().equals(dto.getAddress()))
                                .findFirst()
                                .orElseThrow(RuntimeException::new))
                        .value(dto.getValue())
                        .build())
                .collect(Collectors.toList());

        log.info("Saving " + logs.size() + " reports");
        temperatureLogRepository.saveAll(logs);
    }

    @Transactional()
    public List<SensorWithLogsDto> findLaterThan(Date laterThanDate) {
        List<TemperatureLog> temperatureLogs = temperatureLogRepository.findLaterThan(laterThanDate);
        List<SensorWithLogsDto> sensors = temperatureLogs.stream()
                .map(TemperatureLog::getSensor)
                .distinct()
                .map(SensorWithLogsDto::createWithoutLogs)
                .collect(Collectors.toList());

        sensors.forEach(sensor -> {
            List<TemperatureLogDto> logs = temperatureLogs.stream()
                    .filter(temperatureLog -> temperatureLog.getSensor().getId().equals(sensor.getId()))
                    .map(TemperatureLogDto::create)
                    .collect(Collectors.toList());
            sensor.setLogs(logs);
        });

        return sensors;
    }

    @Transactional
    public List<TemperatureLogDto> getAll() {
        return temperatureLogRepository.findAll().stream()
                .map(TemperatureLogDto::create)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Sensor> getForAllSensors() {
        return sensorRepository.findAll();
    }
}
