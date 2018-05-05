package com.kotlarz.unit.main.service.logs.api.event

import okhttp3.Response
import okhttp3.WebSocket

data class OnOpenEvent(override var webSocket: WebSocket, val response: Response) : LiveTemperatureEvent()