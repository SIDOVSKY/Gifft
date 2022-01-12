package com.gifft.wrapping

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.gifft.core.LifecycleAwareSubscriber
import com.gifft.core.requireNavParam
import com.gifft.core.retain.retain
import com.gifft.core.viewbindingholder.viewBind
import com.gifft.wrapping.databinding.WrappingFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.textChanges
import javax.inject.Inject

internal class WrappingFragment @Inject constructor(
    viewModelFactory: WrappingViewModel.Factory
) : Fragment(R.layout.wrapping_fragment), LifecycleAwareSubscriber {

    private val viewModel by retain {
        viewModelFactory.create(requireNavParam(), retainScope)
    }

    @VisibleForTesting
    val viewBinding by viewBind(WrappingFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(viewBinding!!) {
        super.onViewCreated(view, savedInstanceState)

        sendButton.transitionName = getString(R.string.fab_transition_name)
        sent.text = getString(R.string.wrapping_sent_label, viewModel.sentDate)

        viewModel.state.map { it == WrappingViewModel.VisualState.IN_PROGRESS } observe progress.root::isVisible
        viewModel.sentLabelVisible observe sent::isVisible
        viewModel.sendButtonVisible observe sendButton::isVisible

        viewModel.editingEnabled observe { enabled ->
            sender.isFocusable = enabled
            sender.isEnabled = enabled
            receiver.isFocusable = enabled
            receiver.isEnabled = enabled
            giftText.isFocusable = enabled
            giftText.isEnabled = enabled
        }

        viewModel.sender.filter { it != sender.text?.toString() } observe sender::setText
        viewModel.receiver.filter { it != receiver.text?.toString() } observe receiver::setText
        viewModel.giftContent.filter { it != giftText.text?.toString() } observe giftText::setText

        sendButton.clicks() observe viewModel::onSendGiftClick

        sender.textChanges().skipInitialValue().map { it.toString() } observe viewModel::onSenderInput
        receiver.textChanges().skipInitialValue().map { it.toString() } observe viewModel::onReceiverInput
        giftText.textChanges().skipInitialValue().map { it.toString() } observe viewModel::onGiftContentInput

        viewModel.shareGiftLinkCommand observe { link ->
            startActivity(Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, link)
                type = "text/plain"
            }, null))
        }

        viewModel.showErrorCommand observe { message ->
            AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage(message)
                .show()
        }
    }

    private fun onBackPressed() {
        when (viewModel.exitMode) {
            WrappingViewModel.ExitMode.NoChanges -> {
                parentFragmentManager.popBackStack()
            }
            WrappingViewModel.ExitMode.New -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(getString(R.string.save_new_gift_as_draft))
                    .setNegativeButton(getString(R.string.delete)) { _, _ ->
                        parentFragmentManager.popBackStack()
                    }
                    .setPositiveButton(getString(R.string.save)) { _, _ ->
                        viewModel.onSaveGiftClick()
                        parentFragmentManager.popBackStack()
                    }
                    .show()
            }
            WrappingViewModel.ExitMode.Edited -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(getString(R.string.save_gift_editings))
                    .setNegativeButton(getString(R.string.no)) { _, _ ->
                        parentFragmentManager.popBackStack()
                    }
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel.onSaveGiftClick()
                        parentFragmentManager.popBackStack()
                    }
                    .show()
            }
            WrappingViewModel.ExitMode.Cleaned -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(getString(R.string.gift_cleaned_message))
                    .setNegativeButton(getString(R.string.save_cleaned_gift)) { _, _ ->
                        viewModel.onSaveGiftClick()
                        parentFragmentManager.popBackStack()
                    }
                    .setNeutralButton(getString(R.string.discard_changes)) { _, _ ->
                        parentFragmentManager.popBackStack()
                    }
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        viewModel.onDeleteGiftClick()
                        parentFragmentManager.popBackStack()
                    }
                    .show()
            }
        }
    }
}
