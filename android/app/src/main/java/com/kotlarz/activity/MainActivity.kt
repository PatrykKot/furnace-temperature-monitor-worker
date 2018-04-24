package com.kotlarz.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kotlarz.R
import com.kotlarz.application.FurnaceApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FurnaceApp.graph.inject(this)
    }
}
