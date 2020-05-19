package com.gifft.core.api

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2FragmentClassesAdapter(
    private val pagerHolderFragment: Fragment,
    private vararg val fragmentClasses: Class<out Fragment>
) : FragmentStateAdapter(pagerHolderFragment) {
    override fun getItemCount(): Int = fragmentClasses.size

    override fun createFragment(position: Int): Fragment =
        pagerHolderFragment.childFragmentManager.fragmentFactory.instantiate(
            pagerHolderFragment.requireContext().classLoader,
            fragmentClasses[position].name
        )
}
