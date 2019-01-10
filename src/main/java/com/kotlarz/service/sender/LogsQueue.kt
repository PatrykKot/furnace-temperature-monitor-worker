package com.kotlarz.service.sender

import com.kotlarz.service.domain.TemperatureLogDomain
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*

class LogsQueue {
    companion object {
        private val log: Logger = LogManager.getLogger(LogsQueue::class.java)
    }

    private val logs: MutableList<TemperatureLogDomain> = LinkedList()

    private val cache: LogsCache = LogsCache()

    private val MAX_IN_MEMORY_LOGS = 10

    fun insert(temperatureLog: List<TemperatureLogDomain>) {
        synchronized(logs) {
            logs.addAll(temperatureLog)

            if (logs.size >= MAX_IN_MEMORY_LOGS) {
                log.info("""Filling in disk cache with ${logs.size}""")
                cache.insert(logs)
                logs.clear()
                log.info("""Saved on disk ${cache.size()}""")
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
