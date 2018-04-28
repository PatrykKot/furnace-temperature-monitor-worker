package com.kotlarz.application

import com.beust.jcommander.Parameter

class Arguments {
    @Parameter(names = ["--host"], description = "Server host address", required = true)
    var host: String? = null

    @Parameter(names = ["--port"], description = "Server port", required = true)
    var port: Long? = null

    @Parameter(names = ["--period"], description = "Reading temperature period", required = false)
    var period: Long = 1

    @Parameter(names = ["--mocked"], description = "Should temperature be mocked", required = false)
    var mocked: Boolean = false
}
