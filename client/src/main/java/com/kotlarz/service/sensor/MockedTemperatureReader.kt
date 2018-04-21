package com.kotlarz.service.sensor

import com.kotlarz.service.dto.TemperatureLog

import java.util.Arrays

class MockedTemperatureReader : TemperatureReader {
    override fun readAll(): List<TemperatureLog> {
        return Arrays.asList(TemperatureLog.mock())
    }

    override fun refreshDevices() {}
}
