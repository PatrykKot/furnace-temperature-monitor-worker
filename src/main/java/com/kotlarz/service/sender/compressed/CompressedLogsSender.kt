package com.kotlarz.service.sender.compressed

import com.kotlarz.service.cache.domain.TemperatureLogDomain
import com.kotlarz.service.queue.LogsQueue
import com.kotlarz.service.sender.LogsSender
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class CompressedLogsSender : LogsSender {
    companion object {
        private val log: Logger = LogManager.getLogger(CompressedLogsSender::class.java)

        private const val MAX_LOGS_PER_REQUEST = 10000
    }

    private val queue: LogsQueue = LogsQueue()

    private var runner: Thread = Thread()

    @Synchronized
    override fun send(logs: List<TemperatureLogDomain>) {
        queue.insert(logs)

        if (!runner.isAlive) {
            runner = Thread {
                log.info("Invoking sender")
                call()
            }
            runner.start()
        }
    }

    private fun call() {
        // TODO
    }
}