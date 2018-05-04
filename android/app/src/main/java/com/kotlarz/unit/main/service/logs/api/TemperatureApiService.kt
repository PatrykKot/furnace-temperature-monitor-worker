package com.kotlarz.unit.main.service.logs.api

import com.kotlarz.unit.main.service.logs.dto.SensorWithLogsDto
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface TemperatureApiService {
    @GET("temperatures/sensors/later/{date}")
    fun getTemperaturesLaterThan(@Url baseUrl: String, @Path("date") date: Long): Observable<List<SensorWithLogsDto>> // TODO NOT USED
}