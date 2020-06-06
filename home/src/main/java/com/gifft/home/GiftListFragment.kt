package com.gifft.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ListAdapter
import com.gifft.core.api.autoDispose
import com.gifft.core.api.databinding.ProgressBinding
import com.gifft.core.api.gift.TextGift
import com.gifft.core.api.recycler.setAdapter
import com.gifft.core.api.retain.retain
import com.gifft.core.api.viewbindingholder.viewBind
import com.gifft.home.databinding.GiftListFragmentBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables

abstract class GiftListFragment(
    newViewModel: Lazy<GiftListViewModel>
) : Fragment(R.layout.gift_list_fragment) {

    protected open val viewModel by retain(
        producer = { newViewModel.value },
        onClean = { dispose() })

    protected abstract val giftListAdapter: ListAdapter<TextGift, *>

    private val viewBinding by viewBind(GiftListFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding!!) {
            // ViewBinding import for included layout from different module is not supported yet :(
            val progressBinding = ProgressBinding.bind(root.findViewById(R.id.progress))

            giftList.setAdapter(giftListAdapter, viewLifecycleOwner)

            arrayOf(
                viewModel.gifts
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { giftListAdapter.submitList(it) },

                viewModel.state
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        progressBinding.root.visibility =
                            if (it == GiftListViewModel.VisualState.IN_PROGRESS) View.VISIBLE
                            else View.GONE
                    },

                Observables
                    .combineLatest(viewModel.state, viewModel.gifts) { state, gifts ->
                        if (state == GiftListViewModel.VisualState.DEFAULT && gifts.isNotEmpty())
                            View.VISIBLE
                        else
                            View.GONE
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { giftList.visibility = it },

                Observables
                    .combineLatest(viewModel.state, viewModel.gifts) { state, gifts ->
                        if (state == GiftListViewModel.VisualState.DEFAULT && gifts.isEmpty())
                            View.VISIBLE
                        else
                            View.GONE
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { emptyView.visibility = it }

            ).autoDispose(viewLifecycleOwner, Lifecycle.Event.ON_DESTROY)
        }
    }
}
