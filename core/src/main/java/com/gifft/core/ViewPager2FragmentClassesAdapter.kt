package com.gifft.core

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2FragmentClassesAdapter(
    private val pagerHolderFragment: Fragment,
    private vararg val fragmentClasses: Class<out Fragment>
) : FragmentStateAdapter(pagerHolderFragment) {
    override fun getItemCount(): Int = fragmentClasses.size

    override fun createFragment(position: Int): Fragment {
        val classs =  fragmentClasses[position]

        return pagerHolderFragment.childFragmentManager.fragmentFactory.instantiate(
            classs.classLoader ?: pagerHolderFragment.requireContext().classLoader,
            classs.name
        )
    }
}
