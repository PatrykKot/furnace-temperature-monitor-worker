package com.kotlarz.application;

import com.beust.jcommander.JCommander;

public class App {
    public static void main(String[] args) {
        initApplication(args);
        start();
    }

    private static void initApplication(String[] args) {
        Arguments arguments = new Arguments();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        AppSettings.arguments = arguments;
    }

    private static void start() {
        // TODO
    }
}
