package com.kotlarz.service.sender;

import com.kotlarz.service.dto.TemperatureLog;

import java.util.LinkedList;
import java.util.List;

public class LogsQueue {
    private List<TemperatureLog> logs;

    public LogsQueue() {
        logs = new LinkedList<>();
    }

    public void insert(List<TemperatureLog> temperatureLog) {
        synchronized (logs) {
            logs.addAll(temperatureLog);
        }
    }

    public List<TemperatureLog> get() {
        synchronized (logs) {
            return new LinkedList<>(logs);
        }
    }

    public void remove(List<TemperatureLog> toRemove) {
        synchronized (logs) {
            logs.removeAll(toRemove);
        }
    }
}
