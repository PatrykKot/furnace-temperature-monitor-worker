package com.kotlarz.service.queue

import com.kotlarz.service.cache.PersistentLogsCache
import com.kotlarz.service.cache.domain.TemperatureLogDomain
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger { }

private val MAX_IN_MEMORY_LOGS = 10

class LogsQueue {
    private val logs: MutableList<TemperatureLogDomain> = LinkedList()

    private val cache: PersistentLogsCache = PersistentLogsCache()

    fun insert(temperatureLog: List<TemperatureLogDomain>) {
        synchronized(logs) {
            logs.addAll(temperatureLog)

            if (logs.size >= MAX_IN_MEMORY_LOGS) {
                logger.info { "Filling in disk cache with ${logs.size}" }
                cache.insert(logs)
                logs.clear()
                logger.info { "Saved on disk ${cache.size()}" }
            }
        }
    }

    fun get(): List<TemperatureLogDomain> {
        synchronized(logs) {
            return LinkedList(logs)
        }
    }

    fun remove(toRemove: List<TemperatureLogDomain>) {
        synchronized(logs) {
            logs.removeAll(toRemove)
        }
    }

    fun fromCache(count: Int): List<TemperatureLogDomain> {
        return cache.getLast(count)
    }

    fun removeInCache(values: List<TemperatureLogDomain>) {
        cache.delete(values)
    }

    fun inCache(): Long {
        return cache.size()
    }
}
