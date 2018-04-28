package com.kotlarz.application

import android.app.Application
import com.kotlarz.dependency.AppComponent
import com.kotlarz.dependency.DaggerAppComponent
import io.realm.Realm
import io.realm.RealmConfiguration

open class FurnaceApp : Application() {
    companion object {
        @JvmStatic
        lateinit var graph: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        initRealm()
        buildGraph()
    }

    private fun initRealm() {
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
                //.inMemory()
                .build())
    }

    private fun buildGraph() {
        graph = DaggerAppComponent.builder().build()
    }
}