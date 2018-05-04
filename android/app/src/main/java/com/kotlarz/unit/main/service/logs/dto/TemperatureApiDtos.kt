package com.kotlarz.unit.main.service.logs.dto

import java.util.*

data class TemperatureLogDto(val id: Long, val date: Date, val value: Double) // TODO NOT USED
data class SensorWithLogsDto(val id: Long, val address: String, val logs: List<TemperatureLogDto>) // TODO NOT USED

data class NewTemperatureLog(val date: Date, val address: String, val value: Double)