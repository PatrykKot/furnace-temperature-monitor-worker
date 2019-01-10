package com.kotlarz.service.sensor

import com.kotlarz.service.dto.TemperatureLog

interface TemperatureReader {
    fun readAll(): List<TemperatureLog>

    fun refreshDevices()
}
