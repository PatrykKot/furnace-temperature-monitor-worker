package com.kotlarz.service.sensor;

import com.kotlarz.service.dto.TemperatureLog;
import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TemperatureReader {

    private W1Master master;

    public TemperatureReader() {
        master = new W1Master();
    }

    public List<TemperatureLog> readAll() {
        synchronized (master) {
            return master.getDevices(TmpDS18B20DeviceType.FAMILY_CODE).stream()
                    .map(device -> (TemperatureSensor) device)
                    .map(sensor -> TemperatureLog.builder()
                            .address(((W1Device) sensor).getId())
                            .date(new Date())
                            .value(sensor.getTemperature())
                            .build())
                    .collect(Collectors.toList());
        }
    }

    public void refreshDevices() {
        synchronized (master) {
            master.checkDeviceChanges();
        }
    }
}
