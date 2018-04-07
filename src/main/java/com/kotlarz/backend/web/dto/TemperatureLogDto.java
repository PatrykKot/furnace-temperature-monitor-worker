package com.kotlarz.backend.web.dto;

import com.kotlarz.backend.domain.TemperatureLog;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TemperatureLogDto {
    private Long id;

    private Date date;

    private Double value;

    public static TemperatureLogDto create(TemperatureLog temperatureLog) {
        return TemperatureLogDto.builder()
                .id(temperatureLog.getId())
                .date(temperatureLog.getDate())
                .value(temperatureLog.getValue())
                .build();
    }
}
