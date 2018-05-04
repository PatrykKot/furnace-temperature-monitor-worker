package com.kotlarz.unit.main.service.logs

import com.kotlarz.unit.configuration.service.AppConfigurationService
import com.kotlarz.unit.main.service.logs.api.TemperatureApiService
import com.kotlarz.unit.main.service.logs.dto.SensorWithLogsDto
import io.reactivex.Observable
import java.util.*

class TemperatureLogService(private val apiService: TemperatureApiService,
                            private val appConfigurationService: AppConfigurationService) {
    fun getTemperaturesLaterThan(date: Date): Observable<List<SensorWithLogsDto>> { // TODO NOT USED
        return Observable.fromCallable { appConfigurationService.getConfiguration() }
                .flatMap { configuration ->
                    apiService.getTemperaturesLaterThan(configuration.ipAddress, date.time)
                }
    }
}