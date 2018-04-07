package com.kotlarz.service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Data
@Builder
public class TemperatureLog {
    private static final UUID MOCKED_UUID = UUID.randomUUID();

    private Date date;

    private String address;

    private Double value;

    public static TemperatureLog mock() {
        return TemperatureLog.builder()
                .value(generateRandomTemperature())
                .date(new Date())
                .address(MOCKED_UUID.toString())
                .build();
    }

    private static Double generateRandomTemperature() {
        Double base = 50D;
        Random random = new Random();
        Double bias = random.nextDouble() * 10 - 5;
        return base + bias;
    }
}
