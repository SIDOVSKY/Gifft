package com.gifft.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import com.gifft.gift.api.TextGift
import com.gifft.core.recycler.BindingAdapter
import com.gifft.core.recycler.SequenceDiffCallback
import com.gifft.core.toNavBundle
import com.gifft.home.databinding.GiftListItemReceivedBinding
import com.gifft.unwrapping.api.UnwrappingFragmentProvider
import dagger.Lazy
import java.text.DateFormat
import javax.inject.Inject

internal class ReceivedGiftListFragment @Inject constructor(
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

        item.setOnClickListener { viewModel.onOpenGiftClick(gift) }
        deleteLayout.setOnClickListener { viewModel.onDeleteButtonClick(gift) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.openGiftCommand observe { navParameter ->
            val parentFragment = requireParentFragment()
            parentFragment.parentFragmentManager.commit {
                replace(parentFragment.id, unwrappingFragmentProvider.provideClass(), navParameter.toNavBundle())
                addToBackStack(null)
            }
        }
    }
}
