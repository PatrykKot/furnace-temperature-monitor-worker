package com.kotlarz.application;

import com.kotlarz.service.api.LogsSender;
import com.kotlarz.service.dto.TemperatureLog;
import com.kotlarz.service.sensor.TemperatureReader;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {
    public static void start() {
        LogsSender sender = new LogsSender();
        TemperatureReader reader = null;

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> cycle(sender, reader),
                0, AppSettings.arguments.getPeriod(), TimeUnit.SECONDS);
    }

    private static void cycle(LogsSender sender, TemperatureReader reader) {
        try {
            sender.send(Arrays.asList(TemperatureLog.mock()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
