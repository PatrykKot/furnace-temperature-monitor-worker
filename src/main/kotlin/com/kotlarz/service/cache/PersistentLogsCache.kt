package com.kotlarz.service.cache

import com.kotlarz.service.cache.domain.TemperatureLogDomain
import com.kotlarz.service.database.Database
import mu.KotlinLogging
import org.dizitart.no2.FindOptions
import org.dizitart.no2.objects.ObjectRepository

private val logger = KotlinLogging.logger { }

class PersistentLogsCache {
    private val repository: ObjectRepository<TemperatureLogDomain>

    init {
        logger.info { "Opening database" }
        repository = Database.instance.getRepository(TemperatureLogDomain::class.java)

        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("Closing database")
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
        values.forEach { repository.remove(it) }
    }

    fun size(): Long {
        return repository.size()
    }
}
