package com.kotlarz.application;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {
    public static void main(String[] args) {
        initApplication(args);
    }

    private static void initApplication(String[] args) {
        Arguments arguments = new Arguments();
        JCommander commander = JCommander.newBuilder()
                .addObject(arguments)
                .build();

        if (args.length == 0) {
            commander.usage();
        } else {
            try {
                commander.parse(args);
                AppSettings.arguments = arguments;
                Application.start();
            } catch (ParameterException ex) {
                System.out.print(ex.getMessage());
            }
        }
    }
}
