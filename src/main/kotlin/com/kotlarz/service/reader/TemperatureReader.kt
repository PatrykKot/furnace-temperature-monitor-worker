package com.kotlarz.service.reader

import com.kotlarz.service.cache.domain.TemperatureLogDomain

interface TemperatureReader {
    fun readAll(): List<TemperatureLogDomain>

    fun refreshDevices()
}
