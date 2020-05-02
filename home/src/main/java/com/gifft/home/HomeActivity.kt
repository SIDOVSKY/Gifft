package com.gifft.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity(R.layout.home_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = HomeFragment.Factory()
        super.onCreate(savedInstanceState)

    }
}
