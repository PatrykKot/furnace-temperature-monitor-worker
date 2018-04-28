package com.kotlarz.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.kotlarz.R
import com.kotlarz.application.FurnaceApp
import com.kotlarz.presenter.AppConfigurationPresenter
import kotlinx.android.synthetic.main.activity_configuration.*
import javax.inject.Inject

class ConfigurationActivity : AppCompatActivity() {

    @Inject
    lateinit var configurationPresenter: AppConfigurationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        FurnaceApp.graph.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        configurationPresenter.init(this)
    }

    override fun onDestroy() {
        configurationPresenter.close()
        super.onDestroy()
    }

    fun setBaseUrl(text: String) {
        baseUrlEditText.setText(text, TextView.BufferType.EDITABLE)
    }

    fun getBaseUrl(): String {
        return baseUrlEditText.text.toString()
    }
}
