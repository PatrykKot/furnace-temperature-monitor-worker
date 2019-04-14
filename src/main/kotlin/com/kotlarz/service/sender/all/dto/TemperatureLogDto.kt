package com.kotlarz.service.sender.all.dto

import com.kotlarz.service.cache.domain.TemperatureLogDomain
import java.util.*

fun fromDomain(domain: TemperatureLogDomain) =
        TemperatureLogDto(date = domain.date, address = domain.address, value = domain.value)

data class TemperatureLogDto(val date: Date,
                             val address: String,
                             val value: Double)
