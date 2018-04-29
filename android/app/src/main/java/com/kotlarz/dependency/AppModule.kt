package com.kotlarz.dependency

import com.kotlarz.presenter.AppConfigurationPresenter
import com.kotlarz.presenter.MainPresenter
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

    @Provides
    @Singleton
    fun appConfigurationPresenter(appConfigurationService: AppConfigurationService): AppConfigurationPresenter {
        return AppConfigurationPresenter(appConfigurationService)
    }

    @Provides
    @Singleton
    fun mainPresenter(appConfigurationService: AppConfigurationService): MainPresenter {
        return MainPresenter(appConfigurationService)
    }
}