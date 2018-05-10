package com.kotlarz.application

import com.kotlarz.service.sender.LogsSender
import com.kotlarz.service.sensor.MockedTemperatureReader
import com.kotlarz.service.sensor.RaspberryTemperatureReader
import com.kotlarz.service.sensor.TemperatureReader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object Application {
    private val log: Logger = LogManager.getLogger(Application.javaClass)

    fun start() {
        val sender = LogsSender()
        val reader = if (!AppSettings.arguments.mocked) RaspberryTemperatureReader() else MockedTemperatureReader()

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate({ cycle(sender, reader) },
                0, AppSettings.arguments.period, TimeUnit.SECONDS)

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate({
            log.info("Refreshing devices")
            reader.refreshDevices()
        }, 1, 1, TimeUnit.MINUTES)
    }

    private fun cycle(sender: LogsSender, reader: TemperatureReader) {
        try {
            log.info("Reading temperature")
            val logs = reader.readAll()
            logs.forEach { log -> Application.log.info("Read temperature ${log.value} from address ${log.address}") }
            if (!logs.isEmpty()) {
                sender.invoke(logs)
            } else {
                log.info("No temperature logs found")
            }
        } catch (ex: Exception) {
            log.error(ex.message, ex)
        }
    }
}
