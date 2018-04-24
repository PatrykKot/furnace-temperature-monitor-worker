package com.kotlarz.application

import android.app.Application
import com.kotlarz.dependency.AppComponent
import com.kotlarz.dependency.DaggerAppComponent
import io.realm.Realm

open class FurnaceApp : Application() {
    companion object {
        @JvmStatic
        lateinit var graph: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        buildGraph()
    }

    private fun buildGraph() {
        graph = DaggerAppComponent.builder().build()
    }
}