package com.gifft.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ListAdapter
import com.gifft.core.autoDispose
import com.gifft.gift.api.TextGift
import com.gifft.core.recycler.setAdapter
import com.gifft.core.retain.retain
import com.gifft.core.viewbindingholder.viewBind
import com.gifft.home.databinding.GiftListFragmentBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables

internal abstract class GiftListFragment(
    newViewModel: Lazy<GiftListViewModel>
) : Fragment(R.layout.gift_list_fragment) {

    protected open val viewModel by retain(
        producer = { newViewModel.value },
        onClean = { dispose() })

    protected abstract val giftListAdapter: ListAdapter<TextGift, *>

    private val viewBinding by viewBind(GiftListFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(viewBinding!!) {
        super.onViewCreated(view, savedInstanceState)

        giftList.setAdapter(giftListAdapter, viewLifecycleOwner)

        arrayOf(
            viewModel.gifts
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { giftListAdapter.submitList(it) },

            viewModel.state
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { state ->
                    progress.root.isVisible =
                        state == GiftListViewModel.VisualState.IN_PROGRESS
                },

            Observables
                .combineLatest(viewModel.state, viewModel.gifts) { state, gifts ->
                    state == GiftListViewModel.VisualState.DEFAULT && gifts.isNotEmpty()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { giftList.isVisible = it },

            Observables
                .combineLatest(viewModel.state, viewModel.gifts) { state, gifts ->
                    state == GiftListViewModel.VisualState.DEFAULT && gifts.isEmpty()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { emptyView.isVisible = it }

        ).autoDispose(viewLifecycleOwner)
    }
}
