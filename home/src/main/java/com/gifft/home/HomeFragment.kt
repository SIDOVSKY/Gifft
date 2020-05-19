package com.gifft.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import com.gifft.core.api.ViewPager2FragmentClassesAdapter
import com.gifft.core.api.autoDispose
import com.gifft.core.api.retain.retain
import com.gifft.core.api.viewbindingholder.viewBind
import com.gifft.home.databinding.HomeFragmentBinding
import com.gifft.wrapping.api.WrappingFragmentProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import dagger.Lazy

class HomeFragment @Inject constructor(
    newViewModel: Lazy<HomeViewModel>,
    private val wrappingFragmentProvider: WrappingFragmentProvider
) : Fragment(R.layout.home_fragment) {

    private val viewModel by retain { newViewModel.get() }

    private val viewBinding by viewBind(HomeFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        childFragmentManager.fragmentFactory = parentFragmentManager.fragmentFactory
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding!!) {
            pager.adapter = ViewPager2FragmentClassesAdapter(
                this@HomeFragment,
                CreatedGiftListFragment::class.java,
                ReceivedGiftListFragment::class.java
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
}
