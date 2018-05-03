package com.kotlarz.service.dto

import java.util.*

data class TemperatureLogDto(val id: Long, val date: Date, val value: Double)
data class SensorWithLogsDto(val id: Long, val address: String, val logs: List<TemperatureLogDto>)

data class NewTemperatureLog(val date: Date, val address: String, val value: Double)