package com.gifft.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ListAdapter
import com.gifft.core.LifecycleAwareSubscriber
import com.gifft.gift.api.TextGift
import com.gifft.core.recycler.setAdapter
import com.gifft.core.retain.retain
import com.gifft.core.viewbinding.viewBind
import com.gifft.home.databinding.GiftListFragmentBinding
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

internal abstract class GiftListFragment(
    newViewModel: GiftListViewModel.Factory
) : Fragment(R.layout.gift_list_fragment), LifecycleAwareSubscriber {

    protected open val viewModel by retain { newViewModel.create(retainScope) }

    protected abstract val giftListAdapter: ListAdapter<TextGift, *>

    private val viewBinding by viewBind(GiftListFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(viewBinding!!) {
        super.onViewCreated(view, savedInstanceState)

        giftList.setAdapter(giftListAdapter, viewLifecycleOwner)

        viewModel.gifts observe giftListAdapter::submitList
        viewModel.state.map { it == GiftListViewModel.VisualState.IN_PROGRESS } observe progress.root::isVisible

        combine(viewModel.state, viewModel.gifts) { state, gifts ->
            state == GiftListViewModel.VisualState.DEFAULT && gifts.isNotEmpty()
        } observe giftList::isVisible

        combine(viewModel.state, viewModel.gifts) { state, gifts ->
            state == GiftListViewModel.VisualState.DEFAULT && gifts.isEmpty()
        } observe emptyView::isVisible
    }
}
