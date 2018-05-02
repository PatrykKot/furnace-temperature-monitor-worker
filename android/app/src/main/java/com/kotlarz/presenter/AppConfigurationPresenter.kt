package com.kotlarz.presenter

import android.graphics.Typeface
import android.view.Gravity
import android.view.MenuItem
import com.github.johnpersano.supertoasts.library.Style
import com.github.johnpersano.supertoasts.library.SuperActivityToast
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
                    val viewConfig = getConfigurationFromView(configurationActivity)
                    viewConfig.uuid = appConfigurationService.getConfiguration().uuid
                    viewConfig
                }
                .doOnNext { configuration ->
                    appConfigurationService.save(configuration)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { configuration ->
                    createInfoToast(configurationActivity, Style.green(), R.string.saved).show()
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
                val infoToast = createConnectionCheckingToast(context)

                Observable
                        .fromCallable { infoToast.show() }
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(Schedulers.io())
                        .map { getConfigurationFromView(context) }
                        .map { appConfigurationService.checkConnection(it) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { success ->
                            infoToast.dismiss()
                            if (success) {
                                createInfoToast(context, Style.green(), R.string.connectionWorking).show()
                            } else {
                                createInfoToast(context, Style.red(), R.string.connectionNotWorking).show()
                            }
                        }
                true
            }

            else -> {
                false
            }
        }
    }

    private fun createConnectionCheckingToast(context: ConfigurationActivity): SuperActivityToast {
        val infoToast = SuperActivityToast.create(context, Style.grey(), Style.TYPE_PROGRESS_CIRCLE)
        infoToast.progressIndeterminate = true
        infoToast.isIndeterminate = true
        infoToast.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        infoToast.frame = Style.FRAME_KITKAT
        infoToast.animations = Style.ANIMATIONS_FADE
        infoToast.text = context.resources.getString(R.string.checkingConnection)
        infoToast.typefaceStyle = Typeface.BOLD
        return infoToast
    }

    private fun createInfoToast(context: ConfigurationActivity, style: Style, textId: Int): SuperActivityToast {
        val infoToast = SuperActivityToast.create(context, style, Style.TYPE_STANDARD)
        infoToast.text = context.resources.getString(textId)
        infoToast.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        infoToast.frame = Style.FRAME_KITKAT
        infoToast.animations = Style.ANIMATIONS_FADE
        infoToast.typefaceStyle = Typeface.BOLD
        return infoToast
    }
}
