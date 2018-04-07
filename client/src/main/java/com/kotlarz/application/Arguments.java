package com.kotlarz.application;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class Arguments {
    @Parameter(names = {"--host"}, description = "Server host address", required = true)
    private String host;

    @Parameter(names = {"--port"}, description = "Server port", required = true)
    private Long port;

    @Parameter(names = {"--period"}, description = "Reading temperature period", required = true)
    private Long period;
}
