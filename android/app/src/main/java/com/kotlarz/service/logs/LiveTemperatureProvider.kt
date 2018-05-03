package com.kotlarz.service.logs

import android.util.Log
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kotlarz.domain.AppConfigurationDomain
import com.kotlarz.service.dto.NewTemperatureLog
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import okhttp3.*


class LiveTemperatureProvider {
    private val subject: PublishSubject<List<NewTemperatureLog>> = PublishSubject.create()

    private val mapper = ObjectMapper().registerKotlinModule()

    private var webSocket: WebSocket? = null

    fun connect(configuration: AppConfigurationDomain): Observable<List<NewTemperatureLog>> {
        return Observable
                .fromCallable {
                    val request = Request.Builder()
                            .url(configuration.webSocketUrl)
                            .build()

                    val builder = OkHttpClient.Builder()
                    builder.retryOnConnectionFailure(true)
                    val client = builder.build()

                    webSocket = client.newWebSocket(request, object : WebSocketListener() {
                        override fun onMessage(webSocket: WebSocket, text: String) {
                            val logs = mapper.readValue<List<NewTemperatureLog>>(text, object : TypeReference<List<NewTemperatureLog>>() {})
                            subject.onNext(logs)
                        }

                        override fun onOpen(webSocket: WebSocket?, response: Response?) {
                            Log.d(this.javaClass.name, "Websocket connected")
                        }
                    })
                }
                .flatMap { subject }
    }

    fun disconnect() {
        webSocket?.close(1000, "Bye")
    }
}