package com.kotlarz.service.cache

import com.kotlarz.service.cache.domain.TemperatureLogDomain
import com.kotlarz.service.database.Database
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dizitart.no2.FindOptions
import org.dizitart.no2.objects.ObjectRepository

class PersistentLogsCache {
    companion object {
        private val log: Logger = LogManager.getLogger(PersistentLogsCache::class.java)
    }

    private val repository: ObjectRepository<TemperatureLogDomain>

    init {
        log.info("Opening database")
        repository = Database.instance.getRepository(TemperatureLogDomain::class.java)

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
        values.forEach { repository.remove(it) }
    }

    fun size(): Long {
        return repository.size()
    }
}
