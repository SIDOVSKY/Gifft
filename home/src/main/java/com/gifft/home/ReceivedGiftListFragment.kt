package com.gifft.home

import com.gifft.gift.api.TextGift
import com.gifft.core.api.recycler.BindingAdapter
import com.gifft.core.api.recycler.SequenceDiffCallback
import com.gifft.home.databinding.GiftListItemReceivedBinding
import dagger.Lazy
import java.text.DateFormat
import javax.inject.Inject

class ReceivedGiftListFragment @Inject constructor(
    newViewModel: Lazy<ReceivedGiftListViewModel>
) : GiftListFragment(lazy { newViewModel.get() }) {

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
}
