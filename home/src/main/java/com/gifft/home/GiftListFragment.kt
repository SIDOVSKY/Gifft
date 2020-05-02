package com.gifft.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Lifecycle
import com.gifft.core.api.autoDispose
import com.gifft.core.api.databinding.ProgressBinding
import com.gifft.core.api.retain.retain
import com.gifft.core.api.viewbindingholder.viewBind
import com.gifft.home.databinding.GiftListFragmentBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables

class GiftListFragment(
    newViewModel: Lazy<GiftListViewModel>
) : Fragment(R.layout.gift_list_fragment) {

    private val viewModel by retain { newViewModel.value }

    private val viewBinding by viewBind(GiftListFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding!!) {
            // ViewBinding import for included layout from different module is not supported yet :(
            val progressBinding = ProgressBinding.bind(root.findViewById(R.id.progress))

            arrayOf(
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
