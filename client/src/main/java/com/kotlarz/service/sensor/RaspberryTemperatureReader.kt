package com.kotlarz.service.sensor

import com.kotlarz.service.dto.TemperatureLog
import com.pi4j.component.temperature.TemperatureSensor
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType
import com.pi4j.io.w1.W1Device
import com.pi4j.io.w1.W1Master
import java.util.*
import java.util.stream.Collectors

class RaspberryTemperatureReader : TemperatureReader {
    private val master: W1Master = W1Master()

    override fun readAll(): List<TemperatureLog> {
        synchronized(master) {
            return master.getDevices<W1Device>(TmpDS18B20DeviceType.FAMILY_CODE).stream()
                    .map { device -> device as TemperatureSensor }
                    .map { sensor ->
                        TemperatureLog(
                                address = (sensor as W1Device).id,
                                date = Date(),
                                value = sensor.temperature)
                    }
                    .collect(Collectors.toList())
        }
    }

    override fun refreshDevices() {
        synchronized(master) {
            master.checkDeviceChanges()
        }
    }
}
