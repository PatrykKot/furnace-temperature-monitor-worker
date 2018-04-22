package com.kotlarz.dependency

import com.kotlarz.activity.ConfigurationActivity
import com.kotlarz.activity.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun inject(mainActivity: MainActivity)

    fun inject(configurationActivity: ConfigurationActivity)
}