package com.kotlarz.application

import com.beust.jcommander.Parameter

open class Arguments {
    @Parameter(names = ["--host"], description = "Server host address", required = true)
    var host: String = "localhost"

    @Parameter(names = ["--port"], description = "Server port", required = true)
    var port: Long = 8081

    @Parameter(names = ["--period"], description = "Reading temperature period", required = true)
    var period: Long = 1
}
