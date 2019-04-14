package com.kotlarz.application

import com.kotlarz.service.reader.MockedPoznanTemperatureReader
import com.kotlarz.service.reader.MockedTemperatureReader
import com.kotlarz.service.reader.RaspberryTemperatureReader
import com.kotlarz.service.reader.TemperatureReader
import com.kotlarz.service.sender.LogsSender
import com.kotlarz.service.sender.all.AllLogsSender
import mu.KotlinLogging
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger { }

object Application {
    fun start() {
        val sender = AllLogsSender()
        val reader = resolveReader()

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate({ cycle(sender, reader) },
                0, AppSettings.arguments.period, TimeUnit.SECONDS)

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate({
            logger.info { "Refreshing devices" }
            reader.refreshDevices()
        }, 1, 1, TimeUnit.MINUTES)
    }

    private fun resolveReader(): TemperatureReader {
        return if (!AppSettings.arguments.mocked) {
            RaspberryTemperatureReader()
        } else if (AppSettings.arguments.poznanTemperature) {
            MockedPoznanTemperatureReader()
        } else {
            MockedTemperatureReader()
        }
    }

    private fun cycle(sender: LogsSender, reader: TemperatureReader) {
        try {
            logger.info("Reading temperature")
            val logs = reader.readAll()
            logs.forEach { logger.info { "Read temperature ${it.value} from address ${it.address}" } }
            if (logs.isNotEmpty()) {
                sender.send(logs)
            } else {
                logger.info { "No temperature logs found" }
            }
        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
        }
    }
}
