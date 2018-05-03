package com.kotlarz.presenter

import android.view.Menu
import com.github.johnpersano.supertoasts.library.Style
import com.jakewharton.rxbinding2.view.RxMenuItem
import com.jakewharton.rxbinding2.view.RxView
import com.kotlarz.R
import com.kotlarz.activity.ConfigurationActivity
import com.kotlarz.domain.AppConfigurationDomain
import com.kotlarz.helper.ToastHelper
import com.kotlarz.service.configuration.AppConfigurationService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class AppConfigurationPresenter(private val appConfigurationService: AppConfigurationService) {
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun init(configurationActivity: ConfigurationActivity) {
        initSaveButtonEvent(configurationActivity)
        initToolbar(configurationActivity)

        refreshConfiguration(configurationActivity)
    }

    private fun initSaveButtonEvent(context: ConfigurationActivity) {
        val connectableObservable = RxView.clicks(context.appConfigurationSaveButton)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .throttleFirst(3, TimeUnit.SECONDS)
                .map {
                    val viewConfig = getConfigurationFromView(context)
                    viewConfig.uuid = appConfigurationService.getConfiguration().uuid
                    viewConfig
                }
                .map { appConfigurationService.checkConnection(it) }
                .publish()

        compositeDisposable.add(connectableObservable
                .filter { !it }
                .flatMap { saveConfiguration(context) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { configuration ->
                    ToastHelper.createInfoToast(context, Style.green(), R.string.savedButNotWorking).show()
                    setConfigurationInView(context, configuration)
                })

        compositeDisposable.add(connectableObservable
                .filter { it }
                .flatMap { saveConfiguration(context) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { configuration ->
                    ToastHelper.createInfoToast(context, Style.green(), R.string.saved).show()
                    setConfigurationInView(context, configuration)
                })

        connectableObservable.connect()
    }

    private fun saveConfiguration(context: ConfigurationActivity): Observable<AppConfigurationDomain> {
        return Observable
                .fromCallable {
                    val viewConfig = getConfigurationFromView(context)
                    viewConfig.uuid = appConfigurationService.getConfiguration().uuid
                    appConfigurationService.save(viewConfig)
                    viewConfig
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
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

    fun initMenu(context: ConfigurationActivity, menu: Menu) {
        val infoToast = ToastHelper.createConnectionCheckingToast(context)

        val disposable = RxMenuItem.clicks(menu.findItem(R.id.check_connection_action))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .throttleFirst(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { infoToast.show() }
                .observeOn(Schedulers.io())
                .map { getConfigurationFromView(context) }
                .map { appConfigurationService.checkConnection(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { success ->
                    infoToast.dismiss()
                    if (success) {
                        ToastHelper.createInfoToast(context, Style.green(), R.string.connectionWorking).show()
                    } else {
                        ToastHelper.createInfoToast(context, Style.red(), R.string.connectionNotWorking).show()
                    }
                }

        compositeDisposable.add(disposable)
    }

    fun close() {
        compositeDisposable.clear()
    }
}
