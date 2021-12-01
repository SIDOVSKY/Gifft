package com.gifft.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import com.gifft.core.autoDispose
import com.gifft.gift.api.TextGift
import com.gifft.core.recycler.BindingAdapter
import com.gifft.core.recycler.SequenceDiffCallback
import com.gifft.core.toNavBundle
import com.gifft.gift.api.GiftType
import com.gifft.home.databinding.GiftListItemCreatedBinding
import com.gifft.wrapping.api.WrappingFragmentProvider
import dagger.Lazy
import io.reactivex.android.schedulers.AndroidSchedulers
import java.text.DateFormat
import javax.inject.Inject

internal class CreatedGiftListFragment @Inject constructor(
    newViewModel: Lazy<CreatedGiftListViewModel>,
    private val wrappingFragmentProvider: WrappingFragmentProvider
) : GiftListFragment(lazy { newViewModel.get() }) {

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

        arrayOf(
            viewModel.openGiftCommand
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    requireParentFragment().run {
                        parentFragmentManager.commit {
                            replace(id, wrappingFragmentProvider.provideClass(), it.toNavBundle())
                            addToBackStack(null)
                        }
                    }
                }
        ).autoDispose(viewLifecycleOwner, Lifecycle.Event.ON_DESTROY)
    }
}
