package com.kotlarz.service.sender

import com.kotlarz.service.domain.TemperatureLogDomain
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dizitart.no2.FindOptions
import org.dizitart.no2.objects.ObjectRepository
import java.io.File

class LogsCache {
    companion object {
        private val log: Logger = LogManager.getLogger(LogsCache::class.java)
    }

    private val database = org.dizitart.kno2.nitrite {
        compress = true
        file = File("database.db")
    }

    private val repository: ObjectRepository<TemperatureLogDomain>

    init {
        log.info("Opening database")
        repository = database.getRepository(TemperatureLogDomain::class.java)

        Runtime.getRuntime().addShutdownHook(Thread {
            log.info("Closing database")
            repository.close()
        })
    }

    fun insert(values: List<TemperatureLogDomain>) {
        repository.insert(values.toTypedArray())
    }

    fun getLast(count: Int): List<TemperatureLogDomain> {
        val options = FindOptions.limit(0, count)
        return repository.find(options).toList()
    }

    fun delete(values: List<TemperatureLogDomain>) {
        values.forEach({ repository.remove(it) })
    }

    fun size(): Long {
        return repository.size()
    }
}
