package com.gifft.unwrapping

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.gifft.core.api.toNavBundle
import com.gifft.unwrapping.api.UnwrappingNavParam
import com.gifft.unwrapping.di.GiftLinkComponent
import javax.inject.Inject

class GiftLinkActivity : AppCompatActivity(R.layout.gift_link_activity) {

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        GiftLinkComponent.create(application).inject(this)

        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val link = intent.dataString

            if (link == null)
            {
                startActivity(packageManager.getLaunchIntentForPackage(packageName))
                finish()
            }
            else {
                supportFragmentManager.commit {
                    add<UnwrappingFragment>(
                        R.id.unwrap_fragment_container,
                        null,
                        UnwrappingNavParam(link).toNavBundle()
                    )
                }
            }
        }
    }
}
