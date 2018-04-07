package com.kotlarz.service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TemperatureLog {
    private Date date;

    private String address;

    private Double value;
}
