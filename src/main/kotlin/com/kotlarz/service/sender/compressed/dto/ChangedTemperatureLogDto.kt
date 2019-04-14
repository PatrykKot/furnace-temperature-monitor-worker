package com.kotlarz.service.sender.compressed.dto

import com.kotlarz.service.cache.domain.TemperatureLogDomain
import java.util.*

data class ChangedTemperatureLogDto(val date: Date,
                                    val address: String,
                                    val value: Double)

fun fromDomain(domain: TemperatureLogDomain) =
        ChangedTemperatureLogDto(
                date = domain.date,
                address = domain.address,
                value = domain.value
        )
