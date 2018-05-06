package com.kotlarz.unit.main.presenter

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.kotlarz.R
import com.kotlarz.unit.configuration.activity.ConfigurationActivity
import com.kotlarz.unit.configuration.service.AppConfigurationService
import com.kotlarz.unit.main.activity.MainActivity
import com.kotlarz.unit.main.helper.TemperatureFragmentManager
import com.kotlarz.unit.main.service.logs.TemperatureLogService
import com.kotlarz.unit.main.service.logs.api.event.OnClosedEvent
import com.kotlarz.unit.main.service.logs.api.event.OnFailureEvent
import com.kotlarz.unit.main.service.logs.api.event.OnMessageEvent
import com.kotlarz.unit.main.service.logs.api.event.OnOpenEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainPresenter(private val appConfigurationService: AppConfigurationService,
                    private val temperatureLogService: TemperatureLogService) {
    private var compositeDisposable = CompositeDisposable()

    private var temperatureFragmentManager: TemperatureFragmentManager? = null

    fun init(context: MainActivity) {
        redirectIfNeeded(context)
        initLiveTemperatures(context)

        temperatureFragmentManager = TemperatureFragmentManager(context)
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
        val sharedObservable = temperatureLogService.obtainLiveTemperatures()
                .subscribeOn(Schedulers.io())
                .share()

        compositeDisposable.add(sharedObservable
                .ofType(OnOpenEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { context.connectionStatus.setText(R.string.connected) })

        compositeDisposable.add(sharedObservable
                .ofType(OnClosedEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { context.connectionStatus.setText(R.string.closed) })

        compositeDisposable.add(sharedObservable
                .ofType(OnFailureEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { context.connectionStatus.setText(R.string.connectionFailure) })

        compositeDisposable.add(sharedObservable
                .ofType(OnMessageEvent::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { event ->
                    temperatureFragmentManager?.register(event.logs)
                })
    }

    fun close(mainActivity: MainActivity) {
        compositeDisposable.clear()
    }
}