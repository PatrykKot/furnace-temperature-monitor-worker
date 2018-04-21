package com.kotlarz.service.sender

import com.kotlarz.service.dto.TemperatureLog
import java.util.*

class LogsQueue {
    private val logs: MutableList<TemperatureLog> = LinkedList()

    private val cache: LogsCache = LogsCache()

    private val MAX_IN_MEMORY_LOGS = 10

    fun insert(temperatureLog: List<TemperatureLog>) {
        synchronized(logs) {
            logs.addAll(temperatureLog)

            if (logs.size >= MAX_IN_MEMORY_LOGS) {
                println("""Filling in disk cache with ${logs.size}""")
                cache.insert(logs)
                logs.clear()
                println("""Saved on disk ${cache.size()}""")
            }
        }
    }

    fun get(): List<TemperatureLog> {
        synchronized(logs) {
            return LinkedList(logs)
        }
    }

    fun remove(toRemove: List<TemperatureLog>) {
        synchronized(logs) {
            logs.removeAll(toRemove)
        }
    }

    fun fromCache(count: Int) : List<TemperatureLog> {
        return cache.getLast(count)
    }

    fun removeInCache(values : List<TemperatureLog>) {
        cache.delete(values)
    }

    fun inCache() : Long {
        return cache.size()
    }
}
