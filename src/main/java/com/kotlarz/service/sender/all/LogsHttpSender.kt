package com.kotlarz.service.sender.all

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlarz.application.AppSettings
import com.kotlarz.service.cache.domain.TemperatureLogDomain
import com.kotlarz.service.sender.all.dto.fromDomain
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import java.io.IOException
import java.io.UnsupportedEncodingException

class LogsHttpSender {
    private val mapper: ObjectMapper = ObjectMapper()

    private val client: CloseableHttpClient = HttpClientBuilder
            .create()
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(60 * 1000)
                    .setConnectionRequestTimeout(60 * 1000)
                    .setSocketTimeout(60 * 1000)
                    .build())
            .disableAutomaticRetries()
            .build()

    @Throws(IOException::class)
    fun send(toSend: List<TemperatureLogDomain>) {
        val post = createPost(toSend, buildBaseUrl())

        try {
            val response = client.execute(post)
            val statusCode = response.statusLine.statusCode
            if (statusCode != 200) {
                throw RuntimeException("""Server responded with error: ${response.statusLine}""")
            }
        } finally {
            post.releaseConnection()
        }
    }

    @Throws(UnsupportedEncodingException::class, JsonProcessingException::class)
    private fun createPost(temperatureLogs: List<TemperatureLogDomain>, baseUrl: String): HttpPost {
        val post = HttpPost("$baseUrl/temperatures/uncompressed")
        post.entity = createEntity(temperatureLogs)
        post.setHeader("Content-type", "application/json")
        return post
    }

    @Throws(UnsupportedEncodingException::class, JsonProcessingException::class)
    private fun createEntity(temperatureLogs: List<TemperatureLogDomain>): StringEntity {
        val temperatureLogDtos = temperatureLogs.map { domain -> fromDomain(domain) }
        return StringEntity(mapper.writeValueAsString(temperatureLogDtos))
    }

    private fun buildBaseUrl(): String {
        val protocol = if (AppSettings.arguments.ssl) "https" else "http"
        return """$protocol://${AppSettings.arguments.host}:${AppSettings.arguments.port}/furnace"""
    }
}