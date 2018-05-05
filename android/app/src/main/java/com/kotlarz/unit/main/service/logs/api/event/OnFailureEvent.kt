package com.kotlarz.unit.main.service.logs.api.event

import okhttp3.Response
import okhttp3.WebSocket

data class OnFailureEvent(override var webSocket: WebSocket, val error: Throwable, val response: Response?) : LiveTemperatureEvent()