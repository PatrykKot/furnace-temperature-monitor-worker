package com.kotlarz.application

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        initApplication(args)
    }

    private fun initApplication(args: Array<String>) {
        val arguments = Arguments()
        val commander = JCommander.newBuilder()
                .addObject(arguments)
                .build()

        if (args.isEmpty()) {
            commander.usage()
        } else {
            try {
                commander.parse(*args)
                AppSettings.arguments = arguments
                Application.start()
            } catch (ex: ParameterException) {
                print(ex.message)
            }
        }
    }
}
