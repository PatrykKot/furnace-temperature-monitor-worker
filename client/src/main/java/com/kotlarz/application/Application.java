package com.kotlarz.application;

import com.kotlarz.service.dto.TemperatureLog;
import com.kotlarz.service.sender.LogsSender;
import com.kotlarz.service.sensor.MockedTemperatureReader;
import com.kotlarz.service.sensor.RaspberryTemperatureReader;
import com.kotlarz.service.sensor.TemperatureReader;
import com.pi4j.system.SystemInfo;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {
    public static void start() {
        LogsSender sender = new LogsSender();
        TemperatureReader reader = isRunningOnPi() ? new RaspberryTemperatureReader() : new MockedTemperatureReader();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> cycle(sender, reader),
                0, AppSettings.arguments.getPeriod(), TimeUnit.SECONDS);
    }

    private static void cycle(LogsSender sender, TemperatureReader reader) {
        try {
            System.out.println("Reading temperature");
            List<TemperatureLog> logs = reader.readAll();
            if (!logs.isEmpty()) {
                sender.invoke(logs);
            } else {
                System.out.println("No temperature logs found");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static boolean isRunningOnPi() {
        try {
            SystemInfo.BoardType boardType = SystemInfo.getBoardType();
            return boardType.name().toLowerCase().startsWith("raspberry");
        } catch (Exception ex) {
            return false;
        }
    }
}
