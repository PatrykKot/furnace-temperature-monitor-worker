package com.kotlarz.service.sender.all

import com.kotlarz.service.cache.domain.TemperatureLogDomain
import com.kotlarz.service.queue.LogsQueue
import com.kotlarz.service.sender.LogsSender
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

private const val MAX_LOGS_PER_REQUEST = 10000

private const val SLEEP_TIME_AFTER_ERROR_MS = 60000

class AllLogsSender : LogsSender {
    private val queue = LogsQueue()

    private var runner = Thread()

    private val httpSender = LogsHttpSender()

    @Synchronized
    override fun send(logs: List<TemperatureLogDomain>) {
        queue.insert(logs)

        if (!runner.isAlive) {
            runner = Thread {
                logger.info("Invoking sender")
                call()
            }
            runner.start()
        }
    }

    private fun call() {
        try {
            val toSend = queue.get()
            logger.info("""Sending ${toSend.size} logs""")

            httpSender.send(toSend)
            logger.info("Sending success")
            queue.remove(toSend)

            while (true) {
                val fromCache = queue.fromCache(MAX_LOGS_PER_REQUEST)
                if (fromCache.isNotEmpty()) {
                    logger.info("Cached logs to send: " + queue.inCache())
                    logger.info("""Sending logs from cache. Size ${fromCache.size}""")
                    httpSender.send(fromCache)
                    logger.info("Sending cached logs success")
                    queue.removeInCache(fromCache)
                } else {
                    break
                }
            }
        } catch (ex: Exception) {
            logger.error(ex.message)
            logger.info { "Going sleep for $SLEEP_TIME_AFTER_ERROR_MS ms" }
            Thread.sleep(SLEEP_TIME_AFTER_ERROR_MS.toLong())
            logger.info { "Waking up" }
        }
    }
}
