package com.kotlarz.service.logs

import com.kotlarz.service.dto.SensorWithLogsDto
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface TemperatureApiService {
    @GET("temperatures/sensors/later/{date}")
    fun getTemperaturesLaterThan(@Url baseUrl: String, @Path("date") date: Long): Observable<List<SensorWithLogsDto>>
}