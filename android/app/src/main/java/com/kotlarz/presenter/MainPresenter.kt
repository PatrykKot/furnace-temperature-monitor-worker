package com.kotlarz.presenter

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.kotlarz.R
import com.kotlarz.activity.ConfigurationActivity
import com.kotlarz.activity.MainActivity
import com.kotlarz.service.configuration.AppConfigurationService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainPresenter(private val appConfigurationService: AppConfigurationService) {
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun init(mainActivity: MainActivity) {
        redirectIfNeeded(mainActivity)
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
                redirectToConfiguration(context)
                true
            }

            else -> {
                false
            }
        }
    }

    fun close(mainActivity: MainActivity) {
        compositeDisposable.clear()
    }
}