package com.kotlarz.presenter

import android.content.Context
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.kotlarz.R
import com.kotlarz.activity.ConfigurationActivity
import com.kotlarz.service.configuration.AppConfigurationService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_configuration.*

class AppConfigurationPresenter(private val appConfigurationService: AppConfigurationService) {
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun init(configurationActivity: ConfigurationActivity) {
        initSaveButtonEvent(configurationActivity)
        initToolbar(configurationActivity)

        refreshConfiguration(configurationActivity)
    }

    private fun initSaveButtonEvent(configurationActivity: ConfigurationActivity) {
        compositeDisposable.add(RxView.clicks(configurationActivity.appConfigurationSaveButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map {
                    appConfigurationService.getConfiguration()
                }
                .doOnNext { configuration ->
                    configuration.ipAddress = configurationActivity.getIpAddress()
                    configuration.port = configurationActivity.getPort()
                    configuration.protocol = configurationActivity.getProtocol().name

                    appConfigurationService.save(configuration)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {configuration ->
                    Toast.makeText(configurationActivity, R.string.saved, Toast.LENGTH_SHORT).show()

                    configurationActivity.setIpAddress(configuration.ipAddress)
                    configurationActivity.setPort(configuration.port)
                    configurationActivity.setProtocol(configuration.getProtocolType())
                })
    }

    private fun initToolbar(configurationActivity: ConfigurationActivity) {
        configurationActivity.configuration_toolbar.setNavigationOnClickListener {
            configurationActivity.onBackPressed()
        }
    }

    private fun refreshConfiguration(configurationActivity: ConfigurationActivity) {
        Observable
                .fromCallable { appConfigurationService.getConfiguration() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { configuration ->
                    configurationActivity.setIpAddress(configuration.ipAddress)
                    configurationActivity.setPort(configuration.port)
                    configurationActivity.setProtocol(configuration.getProtocolType())
                }
    }

    fun close() {
        compositeDisposable.clear()
    }

    fun onOptionsSelected(item: MenuItem, context: Context): Boolean {
        return when (item.itemId) {
            R.id.check_connection_action -> {
                Log.d(this.javaClass.name, "Checking connection") // TODO
                true
            }

            else -> {
                false
            }
        }
    }
}
