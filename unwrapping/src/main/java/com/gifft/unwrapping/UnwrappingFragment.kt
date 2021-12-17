package com.gifft.unwrapping

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.gifft.core.LifecycleAwareSubscriber
import com.gifft.core.requireNavParam
import com.gifft.core.retain.retain
import com.gifft.core.viewbindingholder.viewBind
import com.gifft.gift_ui.GiftLayout
import com.gifft.unwrapping.api.UnwrappingNavParam
import com.gifft.unwrapping.databinding.UnwrappingFragmentBinding
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class UnwrappingFragment @Inject constructor(
    viewModelFactory: UnwrappingViewModel.Factory
) : Fragment(R.layout.unwrapping_fragment), LifecycleAwareSubscriber {

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

        viewModel.state.map { it == UnwrappingViewModel.VisualState.IN_PROGRESS } observe progress.root::isVisible
        viewModel.sender observe sender::setText
        viewModel.receiver observe receiver::setText
        viewModel.giftContent observe giftText::setText

        viewModel.fatalError observe { error ->
            AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage(error)
                .setPositiveButton("OK") { _, _ ->
                    activity?.onBackPressed()
                }
                .show()
        }

        viewModel.goHomeCommand observe {
            if (activity is GiftLinkActivity) {
                activity?.run {
                    startActivity(packageManager.getLaunchIntentForPackage(packageName))
                    finish()
                }
            } else {
                activity?.onBackPressed()
            }
        }

        toAllGiftsButton.clicks() observe viewModel::onGoToAllGiftsClick
    }
}
