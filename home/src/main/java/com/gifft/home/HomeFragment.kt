package com.gifft.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import com.gifft.core.api.ViewPager2FragmentArrayAdapter
import com.gifft.core.api.autoDispose
import com.gifft.core.api.retain.retain
import com.gifft.core.api.underdevelopment.UnderDevelopmentFragment
import com.gifft.core.api.viewbindingholder.viewBind
import com.gifft.home.databinding.HomeFragmentBinding
import com.gifft.wrapping.api.WrappingFragmentProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers

class HomeFragment(
    newViewModel: Lazy<HomeViewModel>,
    private val wrappingFragmentProvider: WrappingFragmentProvider
) : Fragment(R.layout.home_fragment) {

    private val viewModel by retain { newViewModel.value }

    private val viewBinding by viewBind(HomeFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding!!) {
            pager.adapter = ViewPager2FragmentArrayAdapter(
                this@HomeFragment,
                GiftListFragment(lazy { CreatedGiftListViewModel() }),
                GiftListFragment(lazy { ReceivedGiftListViewModel() })
            )

            TabLayoutMediator(tabs, pager,
                TabConfigurationStrategy { tab, position ->
                    tab.text = when (position) {
                        0 -> "CREATED"
                        1 -> "RECEIVED"
                        else -> throw IllegalStateException("Unknown tab at position $position")
                    }
                }
            ).attach()

            arrayOf(
                wrapButton.clicks()
                    .subscribe(viewModel.wrapButtonClick),

                viewModel.openWrappingCommand
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        parentFragmentManager.commit {
                            replace(id, wrappingFragmentProvider.provideClass(), null)
                            addToBackStack(null)
                        }
                    }
            ).autoDispose(viewLifecycleOwner, Lifecycle.Event.ON_DESTROY)
        }
    }

    // Temporary until dagger introduced
    class Factory : FragmentFactory() {
        private val wrappingFragmentProviderStub = object : WrappingFragmentProvider {
            override fun provideClass(): Class<out Fragment> = UnderDevelopmentFragment::class.java
        }

        override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
            when (className) {
                HomeFragment::class.java.name -> HomeFragment(
                    lazy { HomeViewModel() },
                    wrappingFragmentProviderStub
                )
                UnderDevelopmentFragment::class.java.name -> UnderDevelopmentFragment()
                else -> throw IllegalStateException("Fragment of type $className is not supported")
            }
    }
    //
}
