package com.kotlarz.service.sender

import com.kotlarz.service.cache.domain.TemperatureLogDomain

interface LogsSender {
    fun send(logs: List<TemperatureLogDomain>)
}