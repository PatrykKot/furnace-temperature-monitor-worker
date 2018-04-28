package com.kotlarz.service.sender

import com.kotlarz.service.dto.TemperatureLog
import org.dizitart.no2.FindOptions
import org.dizitart.no2.objects.ObjectRepository
import java.io.File

class LogsCache {
    private val database = org.dizitart.kno2.nitrite {
        compress = true
        file = File("database.db")
    }

    private val repository: ObjectRepository<TemperatureLog>

    init {
        println("Opening database")
        repository = database.getRepository(TemperatureLog::class.java)

        Runtime.getRuntime().addShutdownHook(Thread {
            println("Closing database")
            repository.close()
        })
    }

    fun insert(values: List<TemperatureLog>) {
        repository.insert(values.toTypedArray())
    }

    fun getLast(count: Int): List<TemperatureLog> {
        val options = FindOptions.limit(0, count)
        return repository.find(options).toList()
    }

    fun delete(values: List<TemperatureLog>) {
        values.forEach({ repository.remove(it) })
    }

    fun size(): Long {
        return repository.size()
    }
}
