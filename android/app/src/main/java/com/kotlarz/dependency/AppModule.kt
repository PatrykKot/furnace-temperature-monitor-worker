package com.kotlarz.dependency

import com.kotlarz.service.configuration.AppConfigurationService
import com.kotlarz.service.logs.TemperatureApiService
import com.kotlarz.service.logs.TemperatureLogService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun retrofit(): Retrofit {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://localhost:8080")
                .build()
    }

    @Provides
    @Singleton
    fun temperatureApiService(retrofit: Retrofit): TemperatureApiService {
        return retrofit.create(TemperatureApiService::class.java)
    }

    @Provides
    @Singleton
    fun temperatureLogService(apiService: TemperatureApiService, appConfigurationService: AppConfigurationService): TemperatureLogService {
        return TemperatureLogService(apiService, appConfigurationService)
    }

    @Provides
    @Singleton
    fun appConfigurationService(): AppConfigurationService {
        return AppConfigurationService()
    }
}