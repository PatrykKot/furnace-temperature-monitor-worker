package com.kotlarz.application;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
class Arguments {
    @Parameter(names = {"--ip"}, description = "Server ip address", required = true)
    private String ip;

    @Parameter(names = {"--port"}, description = "Server port", required = true)
    private Long port;
}
