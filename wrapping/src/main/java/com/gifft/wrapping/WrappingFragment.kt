package com.gifft.wrapping

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.gifft.core.api.autoDispose
import com.gifft.core.api.requireNavParam
import com.gifft.core.api.retain.retain
import com.gifft.core.api.viewbindingholder.viewBind
import com.gifft.wrapping.api.WrappingNavParam
import com.gifft.wrapping.databinding.WrappingFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class WrappingFragment @Inject constructor(
    viewModelFactory: WrappingViewModel.Factory
) : Fragment(R.layout.wrapping_fragment) {

    private val viewModel by retain {
        viewModelFactory.create(requireNavParam<WrappingNavParam>())
    }

    private val viewBinding by viewBind(WrappingFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding!!) {
            arrayOf(
                sendButton.clicks()
                    .subscribe(viewModel.sendButtonClick),

                sender.textChanges()
                    .skip(1)
                    .map { it.toString() }
                    .subscribe(viewModel.senderInput),

                receiver.textChanges()
                    .skip(1)
                    .map { it.toString() }
                    .subscribe(viewModel.receiverInput),

                giftText.textChanges()
                    .skip(1)
                    .map { it.toString() }
                    .subscribe(viewModel.giftContentInput),

                viewModel.sender
                    .filter { sender.text?.toString() != it }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { sender.setText(it) },

                viewModel.receiver
                    .filter { receiver.text?.toString() != it }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { receiver.setText(it) },

                viewModel.giftContent
                    .filter { giftText.text?.toString() != it }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { giftText.setText(it) }
            ).autoDispose(viewLifecycleOwner, Lifecycle.Event.ON_DESTROY)
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
                        viewModel.saveButtonClick.accept(Unit)
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
                        viewModel.saveButtonClick.accept(Unit)
                        parentFragmentManager.popBackStack()
                    }
                    .show()
            }
            WrappingViewModel.ExitMode.Cleaned -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(getString(R.string.gift_cleaned_message))
                    .setNegativeButton(getString(R.string.save_cleaned_gift)) { _, _ ->
                        viewModel.saveButtonClick.accept(Unit)
                        parentFragmentManager.popBackStack()
                    }
                    .setNeutralButton(getString(R.string.discard_changes)) { _, _ ->
                        parentFragmentManager.popBackStack()
                    }
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        viewModel.deleteButtonClick.accept(Unit)
                        parentFragmentManager.popBackStack()
                    }
                    .show()
            }
        }
    }
}
