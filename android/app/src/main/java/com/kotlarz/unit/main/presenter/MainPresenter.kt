package com.kotlarz.unit.main.presenter

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.kotlarz.R
import com.kotlarz.unit.configuration.activity.ConfigurationActivity
import com.kotlarz.unit.configuration.service.AppConfigurationService
import com.kotlarz.unit.main.activity.MainActivity
import com.kotlarz.unit.main.service.logs.TemperatureLogService
import com.kotlarz.unit.main.service.logs.api.LiveTemperatureProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainPresenter(private val appConfigurationService: AppConfigurationService,
                    private val temperatureLogService: TemperatureLogService) {
    private var compositeDisposable = CompositeDisposable()

    private var liveTemperatureProvider = LiveTemperatureProvider()

    fun init(mainActivity: MainActivity) {
        redirectIfNeeded(mainActivity)

        initLiveTemperatures(mainActivity)
    }

    private fun redirectIfNeeded(context: Context) {
        Observable
                .fromCallable {
                    appConfigurationService.isConfigured()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isConfigured ->
                    if (!isConfigured) {
                        redirectToConfiguration(context)
                    }
                }
    }

    private fun redirectToConfiguration(context: Context) {
        val configurationIntent = Intent(context, ConfigurationActivity::class.java)
        context.startActivity(configurationIntent)
    }

    fun onOptionsSelected(item: MenuItem, context: Context): Boolean {
        return when (item.itemId) {
            R.id.action_configuration -> {
                redirectToConfiguration(context) // TODO RX
                true
            }

            else -> {
                false
            }
        }
    }

    private fun initLiveTemperatures(context: MainActivity) {
        Observable
                .fromCallable { appConfigurationService.getConfiguration() }
                .subscribeOn(Schedulers.io())
                .flatMap { liveTemperatureProvider.connect(it) }
                .subscribe { initLiveTemperaturesHandlers(context) }
    }

    private fun initLiveTemperaturesHandlers(context: MainActivity) {
        val onOpenDisposable = liveTemperatureProvider.onOpen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { context.connectionStatus.setText(R.string.connected) }
        compositeDisposable.add(onOpenDisposable)

        val onFailureDisposable = liveTemperatureProvider.onFailure()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { context.connectionStatus.setText(R.string.connectionFailure) }
        compositeDisposable.add(onFailureDisposable)
    }

    fun close(mainActivity: MainActivity) {
        liveTemperatureProvider.disconnect()
                .subscribeOn(Schedulers.io())
                .subscribe()

        compositeDisposable.clear()
    }
}