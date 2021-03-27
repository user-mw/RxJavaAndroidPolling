package com.workout.rxjavaandroidpolling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.workout.rxjavaandroidpolling.ui.PollingFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            openPollingFragment()
        }
    }

    private fun openPollingFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PollingFragment.newInstance())
            .commit()
    }
}