package com.kotlarz.service.dto

import com.kotlarz.service.domain.TemperatureLogDomain
import java.util.*

fun fromDomain(domain: TemperatureLogDomain): TemperatureLogDto {
    return TemperatureLogDto(date = domain.date, address = domain.address, value = domain.value)
}

data class TemperatureLogDto(val date: Date,
                             val address: String,
                             val value: Double)
