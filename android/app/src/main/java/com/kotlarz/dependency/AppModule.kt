package com.kotlarz.dependency

import com.kotlarz.unit.configuration.presenter.AppConfigurationPresenter
import com.kotlarz.unit.configuration.service.AppConfigurationService
import com.kotlarz.unit.main.presenter.MainPresenter
import com.kotlarz.unit.main.service.logs.TemperatureLogService
import com.kotlarz.unit.main.service.logs.api.LiveTemperatureProvider
import com.kotlarz.unit.main.service.logs.api.TemperatureApiService
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
                .baseUrl("http://localhost")
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
    fun temperatureLogService(apiService: TemperatureApiService,
                              appConfigurationService: AppConfigurationService,
                              liveTemperatureProvider: LiveTemperatureProvider): TemperatureLogService {
        return TemperatureLogService(apiService, appConfigurationService, liveTemperatureProvider)
    }

    @Provides
    @Singleton
    fun appConfigurationService(retrofit: Retrofit): AppConfigurationService {
        return AppConfigurationService(retrofit)
    }

    @Provides
    @Singleton
    fun appConfigurationPresenter(appConfigurationService: AppConfigurationService): AppConfigurationPresenter {
        return AppConfigurationPresenter(appConfigurationService)
    }

    @Provides
    @Singleton
    fun mainPresenter(appConfigurationService: AppConfigurationService, temperatureLogService: TemperatureLogService): MainPresenter {
        return MainPresenter(appConfigurationService, temperatureLogService)
    }

    @Provides
    @Singleton
    fun liveTemperatureProvider(): LiveTemperatureProvider {
        return LiveTemperatureProvider()
    }
}