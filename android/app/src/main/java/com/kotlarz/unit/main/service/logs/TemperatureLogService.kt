package com.kotlarz.unit.main.service.logs

import com.kotlarz.unit.configuration.service.AppConfigurationService
import com.kotlarz.unit.main.service.logs.api.LiveTemperatureProvider
import com.kotlarz.unit.main.service.logs.api.TemperatureApiService
import com.kotlarz.unit.main.service.logs.api.event.LiveTemperatureEvent
import com.kotlarz.unit.main.service.logs.dto.SensorWithLogsDto
import io.reactivex.Observable
import java.util.*

class TemperatureLogService(private val apiService: TemperatureApiService,
                            private val appConfigurationService: AppConfigurationService,
                            private val liveTemperatureProvider: LiveTemperatureProvider) {
    fun getTemperaturesLaterThan(date: Date): Observable<List<SensorWithLogsDto>> { // TODO NOT USED
        return Observable.fromCallable { appConfigurationService.getConfiguration() }
                .flatMap { configuration ->
                    apiService.getTemperaturesLaterThan(configuration.ipAddress, date.time)
                }
    }

    fun obtainLiveTemperatures(): Observable<LiveTemperatureEvent> {
        return Observable.fromCallable { appConfigurationService.getConfiguration() }
                .flatMap {
                    liveTemperatureProvider.connect(it)
                }
    }
}