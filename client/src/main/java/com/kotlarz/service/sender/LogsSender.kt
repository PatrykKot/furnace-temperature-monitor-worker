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
import java.io.IOException
import java.io.UnsupportedEncodingException

class LogsSender {
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
                println("Invoking sender")
                call()
            }
            runner.start()
        }
    }

    private fun call() {
        try {
            val toSend = queue.get()
            println("""Sending ${toSend.size} logs""")

            send(toSend)
            println("Sending success")
            queue.remove(toSend)

            while (true) {
                val fromCache = queue.fromCache(MAX_LOGS_PER_REQUEST)
                if (fromCache.isNotEmpty()) {
                    println("Cached logs to send: " + queue.inCache())
                    println("""Sending logs from cache. Size ${fromCache.size}""")
                    send(fromCache)
                    println("Sending cached logs success")
                    queue.removeInCache(fromCache)
                } else {
                    break
                }
            }

        } catch (ex: Exception) {
            println(ex.message)
        }

    }

    @Throws(IOException::class)
    private fun send(toSend: List<TemperatureLog>) {
        val baseUrl = buildBaseUrl()
        val post = createPost(toSend, baseUrl)
        client.execute(post)
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
