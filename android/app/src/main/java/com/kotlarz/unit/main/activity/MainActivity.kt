package com.kotlarz.unit.main.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.kotlarz.R
import com.kotlarz.application.FurnaceApp
import com.kotlarz.unit.main.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var mainPresenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        FurnaceApp.graph.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        mainPresenter.init(this)
    }

    private fun initView() {
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val acted = mainPresenter.onOptionsSelected(item!!, this)
        return if (!acted) {
            super.onOptionsItemSelected(item)
        } else {
            true
        }
    }

    override fun onDestroy() {
        mainPresenter.close(this)
        super.onDestroy()
    }
}
