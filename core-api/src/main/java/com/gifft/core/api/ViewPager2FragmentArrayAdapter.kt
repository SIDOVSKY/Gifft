package com.gifft.core.api

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2FragmentArrayAdapter(
    pagerHolderFragment: Fragment,
    private vararg val fragments: Fragment
) : FragmentStateAdapter(pagerHolderFragment) {
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
