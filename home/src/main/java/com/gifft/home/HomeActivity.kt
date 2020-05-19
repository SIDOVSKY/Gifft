package com.gifft.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentFactory
import com.gifft.home.di.HomeComponent
import javax.inject.Inject

class HomeActivity : AppCompatActivity(R.layout.home_activity) {

    @Inject lateinit var fragmentFactory: FragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        HomeComponent.create(application).inject(this)

        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)
    }
}
