package com.kotlarz.service.reader

import com.kotlarz.service.cache.domain.TemperatureLogDomain
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*

private val API_KEY = "d81f166b649b2111aab1da73f09c2307"

private val SENSOR_ID = "poznan"

class MockedPoznanTemperatureReader : TemperatureReader {
    private val client: CloseableHttpClient = HttpClientBuilder
            .create()
            .build()

    override fun readAll(): List<TemperatureLogDomain> {
        val url = URIBuilder("http://api.openweathermap.org/data/2.5/weather")
                .setParameter("APPID", API_KEY)
                .setParameter("q", "Poznan")
                .build()

        val get = HttpGet(url)

        try {
            val response = client.execute(get)
            val statusCode = response.statusLine.statusCode
            if (statusCode != 200) {
                throw RuntimeException("""Server responded with error: ${response.statusLine}""")
            }

            val json = IOUtils.toString(response.entity.content, StandardCharsets.UTF_8)
            val jsonObj = JSONObject(json)
            val temperature = jsonObj.getJSONObject("main").getDouble("temp") - 273.15

            return Arrays.asList(TemperatureLogDomain(
                    address = SENSOR_ID,
                    date = Date(),
                    value = temperature))
        } finally {
            get.releaseConnection()
        }
    }

    override fun refreshDevices() {
    }
}
