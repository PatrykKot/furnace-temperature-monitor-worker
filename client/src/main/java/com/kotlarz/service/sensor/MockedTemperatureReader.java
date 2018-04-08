package com.kotlarz.service.sensor;

import com.kotlarz.service.dto.TemperatureLog;

import java.util.Arrays;
import java.util.List;

public class MockedTemperatureReader implements TemperatureReader {
    @Override
    public List<TemperatureLog> readAll() {
        return Arrays.asList(TemperatureLog.mock());
    }

    @Override
    public void refreshDevices() {
    }
}
