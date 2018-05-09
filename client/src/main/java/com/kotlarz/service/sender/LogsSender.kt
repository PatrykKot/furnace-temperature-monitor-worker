package com.kotlarz.service.sender

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlarz.application.AppSettings
import com.kotlarz.service.dto.TemperatureLog
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.IOException
import java.io.UnsupportedEncodingException

class LogsSender {
    companion object {
        private val log: Logger = LogManager.getLogger(LogsSender.javaClass)
    }

    private val client: CloseableHttpClient = HttpClientBuilder
            .create()
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(60 * 1000)
                    .setConnectionRequestTimeout(60 * 1000)
                    .setSocketTimeout(60 * 1000)
                    .build())
            .disableAutomaticRetries()
            .build()

    private val mapper: ObjectMapper = ObjectMapper()

    private val queue: LogsQueue = LogsQueue()

    private var runner: Thread = Thread()

    private val MAX_LOGS_PER_REQUEST = 10000

    @Synchronized
    operator fun invoke(temperatureLogs: List<TemperatureLog>) {
        queue.insert(temperatureLogs)

        if (!runner.isAlive) {
            runner = Thread {
                log.debug("Invoking sender")
                call()
            }
            runner.start()
        }
    }

    private fun call() {
        try {
            val toSend = queue.get()
            log.debug("""Sending ${toSend.size} logs""")

            send(toSend)
            log.debug("Sending success")
            queue.remove(toSend)

            while (true) {
                val fromCache = queue.fromCache(MAX_LOGS_PER_REQUEST)
                if (fromCache.isNotEmpty()) {
                    log.debug("Cached logs to send: " + queue.inCache())
                    log.debug("""Sending logs from cache. Size ${fromCache.size}""")
                    send(fromCache)
                    log.debug("Sending cached logs success")
                    queue.removeInCache(fromCache)
                } else {
                    break
                }
            }

        } catch (ex: Exception) {
            log.error(ex.message, ex)
        }

    }

    @Throws(IOException::class)
    private fun send(toSend: List<TemperatureLog>) {
        val baseUrl = buildBaseUrl()
        val post = createPost(toSend, baseUrl)

        try {
            client.execute(post)
        } finally {
            post.releaseConnection()
        }
    }

    @Throws(UnsupportedEncodingException::class, JsonProcessingException::class)
    private fun createPost(temperatureLogs: List<TemperatureLog>, baseUrl: String): HttpPost {
        val post = HttpPost("$baseUrl/temperatures")
        post.entity = createEntity(temperatureLogs)
        post.setHeader("Content-type", "application/json")
        return post
    }

    @Throws(UnsupportedEncodingException::class, JsonProcessingException::class)
    private fun createEntity(temperatureLog: List<TemperatureLog>): StringEntity {
        return StringEntity(mapper.writeValueAsString(temperatureLog))
    }

    private fun buildBaseUrl(): String {
        return """http://${AppSettings.arguments.host}:${AppSettings.arguments.port}"""
    }
}
