package com.gifft.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import com.gifft.gift.api.TextGift
import com.gifft.core.recycler.BindingAdapter
import com.gifft.core.recycler.SequenceDiffCallback
import com.gifft.core.toNavBundle
import com.gifft.gift.api.GiftType
import com.gifft.home.databinding.GiftListItemCreatedBinding
import com.gifft.wrapping.api.WrappingFragmentProvider
import java.text.DateFormat
import javax.inject.Inject

internal class CreatedGiftListFragment @Inject constructor(
    viewModelFactory: CreatedGiftListViewModel.Factory,
    private val wrappingFragmentProvider: WrappingFragmentProvider
) : GiftListFragment(viewModelFactory) {

    override val viewModel
        get() = super.viewModel as CreatedGiftListViewModel

    override val giftListAdapter = BindingAdapter(
        GiftListItemCreatedBinding::inflate,
        SequenceDiffCallback<TextGift>(
            identityYields = { yield(it.uuid) },
            contentYields = {
                yield(it.receiver)
                yield(it.date)
                yield(it.text)
            }
        )
    ) { gift, _, _ ->
        receiver.text = gift.receiver.ifBlank { getString(R.string.gift_item_unknown_receiver) }
        date.text = DateFormat.getDateInstance().format(gift.date)
        giftContent.text = gift.text.ifBlank { getString(R.string.gift_item_no_content_to_send) }
        imageLetters.text = when {
            gift.receiver.length >= 2 -> gift.receiver.substring(0, 2)
            gift.receiver.isNotEmpty() -> gift.receiver.substring(0, 1)
            else -> "*"
        }
        sentLabel.isVisible = gift.type == GiftType.Sent

        item.setOnClickListener { viewModel.onOpenGiftClick(gift) }
        deleteLayout.setOnClickListener { viewModel.onDeleteButtonClick(gift) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.openGiftCommand observe { navParameter ->
            val parentFragment = requireParentFragment()
            parentFragment.parentFragmentManager.commit {
                replace(parentFragment.id, wrappingFragmentProvider.provideClass(), navParameter.toNavBundle())
                addToBackStack(null)
            }
        }
    }
}
