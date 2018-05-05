package com.kotlarz.unit.main.service.logs.api.event

import com.kotlarz.unit.main.service.logs.dto.NewTemperatureLog
import okhttp3.WebSocket

data class OnMessageEvent(override var webSocket: WebSocket,
                          val logs: List<NewTemperatureLog>) : LiveTemperatureEvent()