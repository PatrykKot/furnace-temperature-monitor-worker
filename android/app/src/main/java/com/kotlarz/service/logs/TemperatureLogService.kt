package com.kotlarz.service.logs

import com.kotlarz.service.configuration.AppConfigurationService
import com.kotlarz.service.dto.SensorWithLogsDto
import io.reactivex.Observable
import java.util.*

class TemperatureLogService(private val apiService: TemperatureApiService,
                            private val appConfigurationService: AppConfigurationService) {
    private val provider = LiveTemperatureProvider()

    fun getTemperaturesLaterThan(date: Date): Observable<List<SensorWithLogsDto>> {
        return Observable.fromCallable { appConfigurationService.getConfiguration() }
                .flatMap { configuration ->
                    apiService.getTemperaturesLaterThan(configuration.ipAddress, date.time)
                }
    }

    /*fun getLiveTemperatures(): Observable<List<NewTemperatureLog>> {
        return Observable.fromCallable { appConfigurationService.getConfiguration() }
                .flatMap { provider.getLiveTemperatures(it) }
    }*/
}