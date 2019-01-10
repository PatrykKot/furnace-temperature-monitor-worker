package com.kotlarz.service.sender.all

import com.kotlarz.service.cache.domain.TemperatureLogDomain
import com.kotlarz.service.queue.LogsQueue
import com.kotlarz.service.sender.LogsHttpSender
import com.kotlarz.service.sender.LogsSender
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class AllLogsSender : LogsSender {
    companion object {
        private val log: Logger = LogManager.getLogger(AllLogsSender::class.java)

        private const val MAX_LOGS_PER_REQUEST = 10000
    }

    private val queue: LogsQueue = LogsQueue()

    private var runner: Thread = Thread()

    private val httpSender: LogsHttpSender = LogsHttpSender()

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
        try {
            val toSend = queue.get()
            log.info("""Sending ${toSend.size} logs""")

            httpSender.send(toSend)
            log.info("Sending success")
            queue.remove(toSend)

            while (true) {
                val fromCache = queue.fromCache(MAX_LOGS_PER_REQUEST)
                if (fromCache.isNotEmpty()) {
                    log.info("Cached logs to send: " + queue.inCache())
                    log.info("""Sending logs from cache. Size ${fromCache.size}""")
                    httpSender.send(fromCache)
                    log.info("Sending cached logs success")
                    queue.removeInCache(fromCache)
                } else {
                    break
                }
            }
        } catch (ex: Exception) {
            log.error(ex.message)
        }
    }
}
