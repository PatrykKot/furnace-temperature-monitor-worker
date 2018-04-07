package com.kotlarz.backend.web.dto;

import com.kotlarz.backend.domain.Sensor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class SensorWithLogsDto {
    private Long id;

    private String address;

    private List<TemperatureLogDto> logs;

    public static SensorWithLogsDto createWithoutLogs(Sensor sensor) {
        return SensorWithLogsDto.builder()
                .address(sensor.getAddress())
                .id(sensor.getId())
                .build();
    }

    public static SensorWithLogsDto create(Sensor sensor) {
        SensorWithLogsDto dto = createWithoutLogs(sensor);
        dto.setLogs(sensor.getTemperatureLogs().stream()
                .map(TemperatureLogDto::create)
                .collect(Collectors.toList()));
        return dto;
    }
}
