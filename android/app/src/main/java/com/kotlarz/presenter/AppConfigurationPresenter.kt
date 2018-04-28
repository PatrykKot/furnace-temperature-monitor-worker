package com.kotlarz.presenter

import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.kotlarz.R
import com.kotlarz.activity.ConfigurationActivity
import com.kotlarz.service.configuration.AppConfigurationService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_configuration.*

class AppConfigurationPresenter(private val appConfigurationService: AppConfigurationService) {
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun init(configurationActivity: ConfigurationActivity) {
        refreshConfiguration(configurationActivity)
        initSaveButtonEvent(configurationActivity)
    }

    private fun refreshConfiguration(configurationActivity: ConfigurationActivity) {
        appConfigurationService.getConfiguration()
                .toObservable()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { configuration ->
                    configurationActivity.setBaseUrl(configuration.baseUrl)
                }
    }

    private fun initSaveButtonEvent(configurationActivity: ConfigurationActivity) {
        compositeDisposable.add(RxView.clicks(configurationActivity.appConfigurationSaveButton)
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    appConfigurationService.getConfiguration().toObservable()
                }
                .flatMap { configuration ->
                    configuration.baseUrl = configurationActivity.getBaseUrl()
                    appConfigurationService.save(configuration).toObservable()
                }
                .subscribe {
                    onConfigurationSaved(configurationActivity)
                })
    }

    private fun onConfigurationSaved(configurationActivity: ConfigurationActivity) {
        Toast.makeText(configurationActivity, R.string.saved, Toast.LENGTH_SHORT).show()
        refreshConfiguration(configurationActivity)
    }

    fun close() {
        compositeDisposable.clear()
    }
}
