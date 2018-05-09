package com.kotlarz.service.sender

import com.kotlarz.service.dto.TemperatureLog
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dizitart.no2.FindOptions
import org.dizitart.no2.objects.ObjectRepository
import java.io.File

class LogsCache {
    companion object {
        private val log: Logger = LogManager.getLogger(LogsCache.javaClass)
    }

    private val database = org.dizitart.kno2.nitrite {
        compress = true
        file = File("database.db")
    }

    private val repository: ObjectRepository<TemperatureLog>

    init {
        log.debug("Opening database")
        repository = database.getRepository(TemperatureLog::class.java)

        Runtime.getRuntime().addShutdownHook(Thread {
            log.debug("Closing database")
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
