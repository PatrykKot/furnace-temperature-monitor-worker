package com.kotlarz.service.sender.compressed.dto

import com.kotlarz.service.cache.domain.TemperatureLogDomain
import java.util.*

fun fromDomain(domain: TemperatureLogDomain): ChangedTemperatureLogDto {
    return ChangedTemperatureLogDto(date = domain.date, address = domain.address, value = domain.value)
}

data class ChangedTemperatureLogDto(val date: Date,
                                    val address: String,
                                    val value: Double)