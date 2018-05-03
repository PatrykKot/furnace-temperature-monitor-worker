package com.kotlarz.service.logs

import com.google.gson.Gson


class LiveTemperatureProvider {
    private val gson = Gson()

    /* fun getLiveTemperatures(configuration: AppConfigurationDomain): Observable<List<NewTemperatureLog>> {
         val webSocket = RxWebsocket.Builder()
                 .build(configuration.webSocketUrl)

         return webSocket.connect()
                 .flatMapPublisher { open -> open.client().listen() }
                 .map { message -> gson.fromJson<List<NewTemperatureLog>>(message.data(), object : TypeToken<List<NewTemperatureLog>>() {}.type) }
                 .toObservable()
     }*/
}