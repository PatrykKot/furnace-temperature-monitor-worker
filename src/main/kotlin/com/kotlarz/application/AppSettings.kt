package com.kotlarz.application

class Arguments {
    var host: String = ""

    var port: Long = 0

    var period: Long = 1

    var mocked: Boolean = false

    var mockedSensors: Long = 1

    var poznanTemperature: Boolean = false

    var ssl: Boolean = false
}

object AppSettings {
    var arguments: Arguments = Arguments()
}
