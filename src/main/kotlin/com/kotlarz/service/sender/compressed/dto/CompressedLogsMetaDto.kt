package com.kotlarz.service.sender.compressed.dto

import java.util.*

data class CompressedLogsMetaDto(val deviceId: String,
                                 val deviceStartedTime: Date,
                                 val logs: List<ChangedTemperatureLogDto>)