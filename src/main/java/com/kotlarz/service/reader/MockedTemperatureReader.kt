package com.kotlarz.service.reader

import com.kotlarz.application.AppSettings
import com.kotlarz.service.cache.domain.TemperatureLogDomain
import java.util.*
import java.util.stream.Collectors
import java.util.stream.LongStream

class MockedTemperatureReader : TemperatureReader {
    private val mockedAdresses = mutableListOf<String>()

    override fun readAll(): List<TemperatureLogDomain> {
        while (mockedAdresses.size < AppSettings.arguments.mockedSensors) {
            mockedAdresses.add(UUID.randomUUID().toString())
        }

        return LongStream.range(0, AppSettings.arguments.mockedSensors)
                .boxed()
                .map { index -> mock(mockedAdresses[index.toInt()]) }
                .collect(Collectors.toList<TemperatureLogDomain>())
    }

    companion object {
        fun mock(address: String): TemperatureLogDomain {
            return TemperatureLogDomain(
                    value = generateRandomTemperature(),
                    date = Date(),
                    address = address)
        }

        private fun generateRandomTemperature(): Double {
            val base = 50.0
            val random = Random()
            val bias = random.nextDouble() * 10 - 5
            return base + bias
        }
    }

    override fun refreshDevices() {}
}
