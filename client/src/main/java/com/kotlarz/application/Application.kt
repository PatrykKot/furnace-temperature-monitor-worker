package com.kotlarz.application

import com.kotlarz.service.sender.LogsSender
import com.kotlarz.service.sensor.MockedTemperatureReader
import com.kotlarz.service.sensor.RaspberryTemperatureReader
import com.kotlarz.service.sensor.TemperatureReader
import com.pi4j.platform.Platform
import com.pi4j.platform.PlatformManager
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object Application {
    private val isRunningOnPi: Boolean
        get() {
            return try {
                val platform = PlatformManager.getPlatform()
                println("Running on $platform")
                platform == Platform.RASPBERRYPI
                return false // TODO FIX
            } catch (ex: Exception) {
                println(ex.message)
                false
            }
        }

    fun start() {
        val sender = LogsSender()
        val reader = if (isRunningOnPi && !AppSettings.arguments.mocked) RaspberryTemperatureReader() else MockedTemperatureReader()

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate({ cycle(sender, reader) },
                0, AppSettings.arguments.period, TimeUnit.SECONDS)

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate({
            println("Refreshing devices")
            reader.refreshDevices()
        }, 1, 1, TimeUnit.MINUTES)
    }

    private fun cycle(sender: LogsSender, reader: TemperatureReader) {
        try {
            println("Reading temperature")
            val logs = reader.readAll()
            logs.forEach { log -> println("Read temperature ${log.value} from address ${log.address}") }
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
