package com.kotlarz.unit.main.service.logs.api

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kotlarz.unit.configuration.domain.AppConfigurationDomain
import com.kotlarz.unit.main.service.logs.api.event.*
import com.kotlarz.unit.main.service.logs.dto.NewTemperatureLog
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.*


class LiveTemperatureProvider {

    private val mapper = ObjectMapper().registerKotlinModule()

    fun connect(configuration: AppConfigurationDomain): Observable<LiveTemperatureEvent> {
        return Flowable
                .create<LiveTemperatureEvent>({ emitter ->
                    // TODO STOMP
                    val request = Request.Builder()
                            .url(configuration.webSocketUrl)
                            .build()

                    val builder = OkHttpClient.Builder()
                    builder.retryOnConnectionFailure(true)
                    val client = builder.build()

                    val webSocket = client.newWebSocket(request, object : WebSocketListener() {
                        override fun onMessage(webSocket: WebSocket, text: String) {
                            val logs = mapper.readValue<List<NewTemperatureLog>>(text, object : TypeReference<List<NewTemperatureLog>>() {})
                            emitter.onNext(OnMessageEvent(webSocket, logs))
                        }

                        override fun onOpen(webSocket: WebSocket, response: Response) {
                            emitter.onNext(OnOpenEvent(webSocket, response))
                        }

                        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                            emitter.onNext(OnClosedEvent(webSocket, code, reason))
                        }

                        override fun onFailure(webSocket: WebSocket, error: Throwable, response: Response?) {
                            emitter.onNext(OnFailureEvent(webSocket, error, response))
                        }
                    })

                    emitter.setCancellable {
                        webSocket.close(1000, "Bye")
                    }
                }, BackpressureStrategy.BUFFER)
                .toObservable()
    }
}