package com.gifft.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.commit
import androidx.transition.Fade
import com.gifft.core.LifecycleAwareSubscriber
import com.gifft.core.ViewPager2FragmentClassesAdapter
import com.gifft.core.retain.retain
import com.gifft.core.toNavBundle
import com.gifft.core.viewbindingholder.viewBind
import com.gifft.home.databinding.HomeFragmentBinding
import com.gifft.wrapping.api.WrappingFragmentProvider
import com.gifft.wrapping.api.WrappingNavParam
import com.google.android.material.tabs.TabLayoutMediator
import com.jakewharton.rxbinding3.view.clicks
import dagger.Lazy
import javax.inject.Inject

internal class HomeFragment @Inject constructor(
    newViewModel: Lazy<HomeViewModel>,
    private val fragmentFactory: FragmentFactory,
    private val wrappingFragmentProvider: WrappingFragmentProvider
) : Fragment(R.layout.home_fragment), LifecycleAwareSubscriber {

    private val viewModel by retain { newViewModel.get() }

    private val viewBinding by viewBind(HomeFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        childFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(viewBinding!!) {
        super.onViewCreated(view, savedInstanceState)

        pager.apply {
            isUserInputEnabled = false
            adapter = ViewPager2FragmentClassesAdapter(
                this@HomeFragment,
                CreatedGiftListFragment::class.java,
                ReceivedGiftListFragment::class.java
            )
        }

        TabLayoutMediator(tabs, pager) { tab, position ->
            tab.text = when (position) {
                0 -> "CREATED"
                1 -> "RECEIVED"
                else -> throw IllegalStateException("Unknown tab at position $position")
            }
        }.attach()

        wrapButton.clicks() observe viewModel::onWrapButtonClick

        viewModel.openWrappingCommand observe {
            parentFragmentManager.commit {
                enterTransition = Fade().addTarget(wrapButton)
                exitTransition = Fade().addTarget(wrapButton)

                wrapButton.transitionName = getString(R.string.fab_transition_name)
                addSharedElement(wrapButton, wrapButton.transitionName)

                val wrappingFragment = fragmentFactory.instantiate(
                    requireContext().classLoader,
                    wrappingFragmentProvider.provideClass().name
                ).apply {
                    arguments = WrappingNavParam(null).toNavBundle()
                    sharedElementEnterTransition = Fade().setDuration(1)
                    sharedElementReturnTransition = Fade()
                    enterTransition = Fade()
                    exitTransition = Fade()
                }

                replace(id, wrappingFragment)
                addToBackStack(null)
            }
        }
    }
}