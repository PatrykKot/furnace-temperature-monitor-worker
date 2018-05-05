package com.kotlarz.unit.main.service.logs.api.event

import okhttp3.WebSocket

data class OnClosedEvent(override var webSocket: WebSocket, val code: Int, val reason: String) : LiveTemperatureEvent()