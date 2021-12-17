package com.gifft.unwrapping

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.gifft.core.autoDispose
import com.gifft.core.requireNavParam
import com.gifft.core.retain.retain
import com.gifft.core.viewbindingholder.viewBind
import com.gifft.gift_ui.GiftLayout
import com.gifft.unwrapping.api.UnwrappingNavParam
import com.gifft.unwrapping.databinding.UnwrappingFragmentBinding
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class UnwrappingFragment @Inject constructor(
    viewModelFactory: UnwrappingViewModel.Factory
) : Fragment(R.layout.unwrapping_fragment) {

    private val viewModel by retain { retainScope ->
        viewModelFactory.create(requireNavParam<UnwrappingNavParam>()).also { viewModel ->
            retainScope.launch { viewModel.init() }
        }
    }

    private var giftOpened = false

    private val viewBinding by viewBind(UnwrappingFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(viewBinding!!) {
        super.onViewCreated(view, savedInstanceState)

        giftLayout.openedChangedListener = GiftLayout.OpenedChangedListener {
            if (!giftOpened) {
                giftOpened = true
                confettiAnimation.playAnimation()
            }
        }

        arrayOf(
            viewModel.state
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { state ->
                    progress.root.isVisible =
                        state == UnwrappingViewModel.VisualState.IN_PROGRESS
                },

            viewModel.sender
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { sender.setText(it) },

            viewModel.receiver
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { receiver.setText(it) },

            viewModel.giftContent
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { giftText.setText(it) },

            viewModel.fatalError
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { error ->
                    AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage(error)
                        .setPositiveButton("OK") { _, _ ->
                            activity?.onBackPressed()
                        }
                        .show()
                },

            viewModel.goHomeCommand
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (activity is GiftLinkActivity) {
                        activity?.run {
                            startActivity(packageManager.getLaunchIntentForPackage(packageName))
                            finish()
                        }
                    } else {
                        activity?.onBackPressed()
                    }
                },

            toAllGiftsButton.clicks()
                .subscribe { viewModel.onGoToAllGiftsClick() },
        ).autoDispose(viewLifecycleOwner)
    }
}
