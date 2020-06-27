package com.gifft.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import com.gifft.core.api.autoDispose
import com.gifft.gift.api.TextGift
import com.gifft.core.api.recycler.BindingAdapter
import com.gifft.core.api.recycler.SequenceDiffCallback
import com.gifft.core.api.toNavBundle
import com.gifft.home.databinding.GiftListItemReceivedBinding
import com.gifft.unwrapping.api.UnwrappingFragmentProvider
import dagger.Lazy
import io.reactivex.android.schedulers.AndroidSchedulers
import java.text.DateFormat
import javax.inject.Inject

class ReceivedGiftListFragment @Inject constructor(
    newViewModel: Lazy<ReceivedGiftListViewModel>,
    private val unwrappingFragmentProvider: UnwrappingFragmentProvider
) : GiftListFragment(lazy { newViewModel.get() }) {

    override val viewModel
        get() = super.viewModel as ReceivedGiftListViewModel

    override val giftListAdapter = BindingAdapter(
        GiftListItemReceivedBinding::inflate,
        SequenceDiffCallback<TextGift>(
            identityYields = { yield(it.uuid) },
            contentYields = {
                yield(it.sender)
                yield(it.date)
                yield(it.text)
            }
        )
    ) { gift, _, _ ->
        sender.text = gift.sender.ifBlank { getString(R.string.gift_item_unknown_sender) }
        date.text = DateFormat.getDateInstance().format(gift.date)
        giftContent.text = gift.text.ifBlank { getString(R.string.gift_item_no_content_received) }
        imageLetters.text = when {
            gift.sender.length >= 2 -> gift.sender.substring(0, 2)
            gift.sender.isNotEmpty() -> gift.sender.substring(0, 1)
            else -> "*"
        }

        item.setOnClickListener { viewModel.openGiftClick.accept(gift) }
        deleteLayout.setOnClickListener { viewModel.deleteButtonClick.accept(gift) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arrayOf(
            viewModel.openGiftCommand
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    requireParentFragment().run {
                        parentFragmentManager.commit {
                            replace(id, unwrappingFragmentProvider.provideClass(), it.toNavBundle())
                            addToBackStack(null)
                        }
                    }
                }
        ).autoDispose(viewLifecycleOwner, Lifecycle.Event.ON_DESTROY)
    }
}
