package com.kotlarz.application

import com.kotlarz.service.sender.LogsSender
import com.kotlarz.service.sensor.MockedTemperatureReader
import com.kotlarz.service.sensor.RaspberryTemperatureReader
import com.kotlarz.service.sensor.TemperatureReader
import com.pi4j.system.SystemInfo
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object Application {
    private val isRunningOnPi: Boolean
        get() {
            return try {
                val boardType = SystemInfo.getBoardType()
                boardType.name.toLowerCase().startsWith("raspberry")
            } catch (ex: Exception) {
                false
            }

        }

    fun start() {
        val sender = LogsSender()
        val reader = if (isRunningOnPi) RaspberryTemperatureReader() else MockedTemperatureReader()

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate({ cycle(sender, reader) },
                0, AppSettings.arguments.period, TimeUnit.SECONDS)
    }

    private fun cycle(sender: LogsSender, reader: TemperatureReader) {
        try {
            println("Reading temperature")
            val logs = reader.readAll()
            if (!logs.isEmpty()) {
                sender.invoke(logs)
            } else {
                println("No temperature logs found")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
