package com.kotlarz.unit.main.service.logs.api.event

import okhttp3.WebSocket

abstract class LiveTemperatureEvent() {
    abstract var webSocket: WebSocket
}