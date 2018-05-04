package com.kotlarz.unit.configuration.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kotlarz.R
import com.kotlarz.application.FurnaceApp
import com.kotlarz.unit.configuration.domain.enumeration.ProtocolType
import com.kotlarz.unit.configuration.presenter.AppConfigurationPresenter
import kotlinx.android.synthetic.main.activity_configuration.*
import javax.inject.Inject

class ConfigurationActivity : AppCompatActivity() {

    @Inject
    lateinit var configurationPresenter: AppConfigurationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        FurnaceApp.graph.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        initView()
        configurationPresenter.init(this)
    }

    private fun initView() {
        initToolbar()
        initSpinner()
    }

    private fun initSpinner() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.protocolSpinnerValues, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        protocolSpinner.adapter = adapter
    }

    private fun initToolbar() {
        setSupportActionBar(configuration_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.configuration_toolbar_menu, menu)
        configurationPresenter.initMenu(this, menu)

        return true
    }

    override fun onDestroy() {
        configurationPresenter.close()
        super.onDestroy()
    }

    var ipAddress: String
        get() {
            return ipAddressEditText.text.toString()
        }
        set(value) {
            ipAddressEditText.setText(value, TextView.BufferType.EDITABLE)
        }

    var port: Long
        get() {
            return try {
                portEditText.text.toString().toLong()
            } catch (ex: NumberFormatException) {
                0
            }
        }
        set(value) {
            portEditText.setText(value.toString(), TextView.BufferType.EDITABLE)
        }

    var protocol: ProtocolType
        get() {
            return ProtocolType.valueOf(protocolSpinner.selectedItem.toString().toUpperCase())
        }
        set(protocolType) {
            val protocolArray = resources.getStringArray(R.array.protocolSpinnerValues)
            val index = protocolArray.indexOf(protocolType.name)
            protocolSpinner.setSelection(index)
        }
}
