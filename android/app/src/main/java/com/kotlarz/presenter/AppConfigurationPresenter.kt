package com.kotlarz.presenter

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
import java.util.concurrent.TimeUnit

class AppConfigurationPresenter(private val appConfigurationService: AppConfigurationService) {
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun init(configurationActivity: ConfigurationActivity) {
        initSaveButtonEvent(configurationActivity)
        initToolbar(configurationActivity)

        refreshConfiguration(configurationActivity)
    }

    private fun initSaveButtonEvent(configurationActivity: ConfigurationActivity) {
        compositeDisposable.add(RxView.clicks(configurationActivity.appConfigurationSaveButton)
                .throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    appConfigurationService.getConfiguration()
                }
                .map { configuration ->
                    configuration.baseUrl = configurationActivity.getBaseUrl()
                    appConfigurationService.save(configuration)
                }
                .subscribe {
                    onConfigurationSaved(configurationActivity)
                })
    }

    private fun initToolbar(configurationActivity: ConfigurationActivity) {
        val toolbar = configurationActivity.configuration_toolbar

        configurationActivity.setSupportActionBar(toolbar)
        configurationActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        configurationActivity.supportActionBar!!.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener { configurationActivity.onBackPressed() }
    }

    private fun refreshConfiguration(configurationActivity: ConfigurationActivity) {
        Observable
                .fromCallable {
                    appConfigurationService.getConfiguration()
                }
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { configuration ->
                    configurationActivity.setBaseUrl(configuration.baseUrl)
                }
    }

    private fun onConfigurationSaved(configurationActivity: ConfigurationActivity) {
        Toast.makeText(configurationActivity, R.string.saved, Toast.LENGTH_SHORT).show()
        refreshConfiguration(configurationActivity)
    }

    fun close() {
        compositeDisposable.clear()
    }
}
