package com.kotlarz.application

import android.app.Application
import com.kotlarz.dependency.AppComponent
import com.kotlarz.dependency.DaggerAppComponent

open class FurnaceApp : Application() {
    companion object {
        @JvmStatic
        lateinit var graph: AppComponent
    }

    override fun onCreate() {
        buildGraph()

        super.onCreate()
    }

    private fun buildGraph() {
        graph = DaggerAppComponent.builder().build()
    }
}