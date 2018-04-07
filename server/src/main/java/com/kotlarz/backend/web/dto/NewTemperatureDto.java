package com.kotlarz.backend.web.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NewTemperatureDto {
    private Date date;

    private String address;

    private Double value;
}
