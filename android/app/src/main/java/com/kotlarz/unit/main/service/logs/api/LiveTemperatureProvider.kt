package com.kotlarz.unit.main.service.logs.api

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kotlarz.unit.configuration.domain.AppConfigurationDomain
import com.kotlarz.unit.main.service.logs.dto.NewTemperatureLog
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import okhttp3.*


class LiveTemperatureProvider {
    private val newMessageSubject = PublishSubject.create<List<NewTemperatureLog>>()

    private val openSubject = PublishSubject.create<WebSocket>()

    private val closedSubject = PublishSubject.create<String>()

    private val failureSubject = PublishSubject.create<Throwable>()

    private val mapper = ObjectMapper().registerKotlinModule()

    private var webSocket: WebSocket? = null

    private var sourceObservable = Observable.empty<WebSocket>()

    fun connect(configuration: AppConfigurationDomain): Observable<WebSocket>? {
        sourceObservable = Observable
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
                            newMessageSubject.onNext(logs)
                        }

                        override fun onOpen(webSocket: WebSocket, response: Response) {
                            openSubject.onNext(webSocket)
                        }

                        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                            closedSubject.onNext(reason)
                        }

                        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                            failureSubject.onNext(t)
                        }
                    })
                    webSocket
                }
                .share()

        return sourceObservable
    }

    fun onOpen(): Observable<WebSocket> {
        return openSubject
    }

    fun onClosed(): Observable<String> {
        return closedSubject
    }

    fun onFailure(): Observable<Throwable> {
        return failureSubject
    }

    fun disconnect(): Observable<Any> {
        return Observable.fromCallable {
            webSocket?.close(1000, "Bye")
        }
    }
}