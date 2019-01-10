package com.kotlarz.service.sensor

import com.kotlarz.service.domain.TemperatureLogDomain

interface TemperatureReader {
    fun readAll(): List<TemperatureLogDomain>

    fun refreshDevices()
}
