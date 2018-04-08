package com.kotlarz.service.sensor;

import com.kotlarz.service.dto.TemperatureLog;

import java.util.List;

public interface TemperatureReader {
    List<TemperatureLog> readAll();

    void refreshDevices();
}
