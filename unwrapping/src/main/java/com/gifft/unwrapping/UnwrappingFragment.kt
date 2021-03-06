package com.gifft.unwrapping

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.gifft.core.api.autoDispose
import com.gifft.core.api.databinding.ProgressBinding
import com.gifft.core.api.requireNavParam
import com.gifft.core.api.retain.retain
import com.gifft.core.api.viewbindingholder.viewBind
import com.gifft.gift_ui.GiftLayout
import com.gifft.unwrapping.api.UnwrappingNavParam
import com.gifft.unwrapping.databinding.UnwrappingFragmentBinding
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UnwrappingFragment @Inject constructor(
    viewModelFactory: UnwrappingViewModel.Factory
) : Fragment(R.layout.unwrapping_fragment) {

    private val viewModel by retain {
        viewModelFactory.create(requireNavParam<UnwrappingNavParam>()).also {
            lifecycleScope.launch {
                it.init()
            }
        }
    }

    private var giftOpened = false

    private val viewBinding by viewBind(UnwrappingFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding!!) {
            // ViewBinding import for included layout from different module is not supported yet :(
            val progressBinding = ProgressBinding.bind(root.findViewById(R.id.progress))

            giftLayout.openedChangedListener = object : GiftLayout.OpenedChangedListener {
                override fun onOpenedChanged(opened: Boolean) {
                    if (!giftOpened) {
                        giftOpened = true
                        confettiAnimation.playAnimation()
                    }
                }
            }

            arrayOf(
                viewModel.state
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        progressBinding.root.visibility =
                            if (it == UnwrappingViewModel.VisualState.IN_PROGRESS) View.VISIBLE
                            else View.GONE
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
                    .subscribe {error ->
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
                        }
                        else {
                            activity?.onBackPressed()
                        }
                    },

                toAllGiftsButton.clicks()
                    .subscribe(viewModel.toAllGiftsButtonClick)
            ).autoDispose(viewLifecycleOwner, Lifecycle.Event.ON_DESTROY)
        }
    }
}
