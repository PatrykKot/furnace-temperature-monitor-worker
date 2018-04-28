package com.kotlarz.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kotlarz.R
import com.kotlarz.application.FurnaceApp
import com.kotlarz.service.configuration.AppConfigurationService
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var appConfigurationService: AppConfigurationService

    override fun onCreate(savedInstanceState: Bundle?) {
        FurnaceApp.graph.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appConfigurationService.isConfigured()
                .subscribe { isConfigured ->
                    if (!isConfigured) {
                        redirectToConfiguration()
                    }
                }
    }

    private fun redirectToConfiguration() {
        val configurationIntent = Intent(this, ConfigurationActivity::class.java)
        startActivity(configurationIntent)
    }
}
