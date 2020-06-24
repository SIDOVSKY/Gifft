package com.gifft.wrapping

import android.annotation.SuppressLint
import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.TextGift
import com.gifft.wrapping.api.WrappingNavParam
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult") // applicable to class only
class WrappingViewModel @AssistedInject constructor(
    @Assisted navParam: WrappingNavParam,
    private val giftRepository: GiftRepository
) {
    @AssistedInject.Factory
    interface Factory {
        fun create(navParam: WrappingNavParam): WrappingViewModel
    }

    private val _senderRelay = BehaviorRelay.createDefault("")
    private val _receiverRelay = BehaviorRelay.createDefault("")
    private val _giftContentRelay = BehaviorRelay.createDefault("")

    private val _sendClickRelay = PublishRelay.create<Unit>()
    private val _saveClickRelay = PublishRelay.create<Unit>()
    private val _deleteClickRelay = PublishRelay.create<Unit>()

    private val _originalGift = navParam.existingGiftUuid?.let {
        giftRepository.findGift(it)
    }

    init {
        _sendClickRelay
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribe { sendGift() }

        _saveClickRelay
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribe { saveGift() }

        _deleteClickRelay
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribe { deleteGift() }

        _originalGift?.let {
            _senderRelay.accept(it.sender)
            _receiverRelay.accept(it.receiver)
            _giftContentRelay.accept(it.text)
        }
    }

    val sender: Observable<String> = _senderRelay.distinctUntilChanged()
    val receiver: Observable<String> = _receiverRelay.distinctUntilChanged()
    val giftContent: Observable<String> = _giftContentRelay.distinctUntilChanged()

    val senderInput: Consumer<String> = _senderRelay
    val receiverInput: Consumer<String> = _receiverRelay
    val giftContentInput: Consumer<String> = _giftContentRelay

    val sendButtonClick: Consumer<Unit> = _sendClickRelay
    val saveButtonClick: Consumer<Unit> = _saveClickRelay
    val deleteButtonClick: Consumer<Unit> = _deleteClickRelay

    val exitMode: ExitMode
        get() {
            val newGift = _originalGift == null
            val emptyContent = _senderRelay.value.isNullOrBlank()
                    && _receiverRelay.value.isNullOrBlank()
                    && _giftContentRelay.value.isNullOrBlank()

            val originalGiftChanged = _originalGift?.run {
                sender != _senderRelay.value
                        || receiver != _receiverRelay.value
                        || text != _giftContentRelay.value
            } ?: false

            return when {
                newGift && !emptyContent -> ExitMode.New
                !newGift && emptyContent -> ExitMode.Cleaned
                originalGiftChanged -> ExitMode.Edited
                else -> ExitMode.NoChanges
            }
        }

    private fun sendGift() {
    }

    private fun saveGift() {
        giftRepository.saveCreatedTextGift(
            TextGift(
                _originalGift?.uuid ?: UUID.randomUUID().toString(),
                _senderRelay.value ?: "",
                _receiverRelay.value ?: "",
                _originalGift?.date ?: Date(),
                _giftContentRelay.value ?: ""
            )
        )
    }

    private fun deleteGift() {
        _originalGift?.let { giftRepository.deleteGift(it.uuid) }
    }

    enum class ExitMode {
        NoChanges,
        New,
        Edited,
        Cleaned
    }
}
