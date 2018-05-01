package com.kotlarz.presenter

import android.view.MenuItem
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.kotlarz.R
import com.kotlarz.activity.ConfigurationActivity
import com.kotlarz.domain.AppConfigurationDomain
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
                    val viewConfig = getConfigurationFromView(configurationActivity)
                    viewConfig.uuid = configuration.uuid

                    appConfigurationService.save(viewConfig)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { configuration ->
                    Toast.makeText(configurationActivity, R.string.saved, Toast.LENGTH_SHORT).show()
                    setConfigurationInView(configurationActivity, configuration)
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
                    setConfigurationInView(configurationActivity, configuration)
                }
    }

    private fun getConfigurationFromView(configurationActivity: ConfigurationActivity): AppConfigurationDomain {
        val config = AppConfigurationDomain()
        config.ipAddress = configurationActivity.ipAddress
        config.port = configurationActivity.port
        config.protocol = configurationActivity.protocol.name
        return config
    }

    private fun setConfigurationInView(configurationActivity: ConfigurationActivity, configuration: AppConfigurationDomain) {
        configurationActivity.ipAddress = configuration.ipAddress
        configurationActivity.port = configuration.port
        configurationActivity.protocol = configuration.protocolType
    }

    fun close() {
        compositeDisposable.clear()
    }

    fun onOptionsSelected(item: MenuItem, context: ConfigurationActivity): Boolean {
        return when (item.itemId) {
            R.id.check_connection_action -> {
                Observable
                        .fromCallable {
                            Toast.makeText(context, R.string.checkingConnection, Toast.LENGTH_SHORT).show()
                        }
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(Schedulers.io())
                        .map { getConfigurationFromView(context) }
                        .map { appConfigurationService.checkConnection(it) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { working ->
                            if (working) {
                                Toast.makeText(context, R.string.connectionWorking, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, R.string.connectionNotWorking, Toast.LENGTH_SHORT).show()
                            }
                        }
                true
            }

            else -> {
                false
            }
        }
    }
}
