package com.kotlarz.service.sender.compressed

import com.kotlarz.service.cache.domain.TemperatureLogDomain
import com.kotlarz.service.queue.LogsQueue
import com.kotlarz.service.sender.LogsSender
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

class CompressedLogsSender : LogsSender {
    private val queue = LogsQueue()

    private var runner = Thread()

    @Synchronized
    override fun send(logs: List<TemperatureLogDomain>) {
        queue.insert(logs)

        if (!runner.isAlive) {
            runner = Thread {
                log.info { "Invoking sender" }
                call()
            }
            runner.start()
        }
    }

    private fun call() {
        // TODO
    }
}
