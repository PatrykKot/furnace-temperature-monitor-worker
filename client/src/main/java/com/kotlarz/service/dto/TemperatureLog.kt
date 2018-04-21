package com.kotlarz.service.dto

import org.dizitart.no2.objects.Id
import java.util.*

data class TemperatureLog(@Id val uuid: String = UUID.randomUUID().toString(),
                          val date: Date,
                          val address: String,
                          val value: Double) {
    companion object {
        private val MOCKED_UUID = UUID.randomUUID()

        fun mock(): TemperatureLog {
            return TemperatureLog(
                    value = generateRandomTemperature(),
                    date = Date(),
                    address = MOCKED_UUID.toString())
        }

        private fun generateRandomTemperature(): Double {
            val base = 50.0
            val random = Random()
            val bias = random.nextDouble() * 10 - 5
            return base + bias
        }
    }
}
