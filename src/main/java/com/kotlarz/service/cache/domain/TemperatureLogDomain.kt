package com.kotlarz.service.cache.domain

import org.dizitart.no2.objects.Id
import java.util.*

data class TemperatureLogDomain(@Id val uuid: String = UUID.randomUUID().toString(),
                                val date: Date,
                                val address: String,
                                val value: Double)
