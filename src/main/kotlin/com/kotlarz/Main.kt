package com.kotlarz

import com.kotlarz.application.Application
import com.kotlarz.application.Arguments

fun main() {
    setupEnvironment()
    Application.start()
}

fun setupEnvironment() {
    val args = Arguments()

    setProperty("HOST", required = true) { args.host = it }
    setProperty("PORT", required = true) { args.port = it.toLong() }
    setProperty("PERIOD") { args.period = it.toLong() }
    setProperty("MOCKED") { args.mocked = it.toBoolean() }
    setProperty("MOCKED_SENSORS") { args.mockedSensors = it.toLong() }
    setProperty("POZNAN_TEMPERATURE") { args.poznanTemperature = it.toBoolean() }
    setProperty("SSL") { args.ssl = it.toBoolean() }
}

fun setProperty(name: String, required: Boolean = false, call: (value: String) -> Unit) {
    val value = System.getProperty(name)
    if (required) {
        assert(value != null) { "System environment property $name cannot be empty" }
    }

    if (value != null) {
        try {
            call(value)
        } catch (ex: Exception) {
            throw RuntimeException("Error while getting property $name", ex)
        }
    }
}
