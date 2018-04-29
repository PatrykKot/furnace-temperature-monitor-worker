package com.kotlarz.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kotlarz.R
import com.kotlarz.application.FurnaceApp
import com.kotlarz.domain.enumeration.ProtocolType
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

    override fun onDestroy() {
        configurationPresenter.close()
        super.onDestroy()
    }

    fun setIpAddress(text: String) {
        ipAddressEditText.setText(text, TextView.BufferType.EDITABLE)
    }

    fun getIpAddress(): String {
        return ipAddressEditText.text.toString()
    }

    fun getPort(): Long {
        return portEditText.text.toString().toLong()
    }

    fun getProtocol(): ProtocolType {
        return ProtocolType.valueOf(protocolSpinner.selectedItem.toString().toUpperCase())
    }

    fun setPort(port: Long) {
        portEditText.setText(port.toString(), TextView.BufferType.EDITABLE)
    }

    fun setProtocol(protocolType: ProtocolType) {
        /* val protocolArray = resources.getStringArray(R.array.protocolSpinnerValues)
         val index = protocolArray.indexOf(protocolType.name)
         protocolSpinner.setSelection(index)*/ // TODO FIX
    }
}
