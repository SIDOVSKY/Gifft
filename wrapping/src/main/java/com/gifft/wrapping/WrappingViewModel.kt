package com.gifft.wrapping

import android.annotation.SuppressLint
import com.gifft.core.api.debounce
import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.GiftType
import com.gifft.gift.api.TextGift
import com.gifft.gift.api.TextGiftLinkBuilder
import com.gifft.wrapping.api.WrappingNavParam
import com.jakewharton.rxrelay2.BehaviorRelay
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("CheckResult") // applicable to class only
class WrappingViewModel @AssistedInject constructor(
    @Assisted navParam: WrappingNavParam,
    @Assisted coroutineScope: CoroutineScope,
    private val giftRepository: GiftRepository,
    private val giftLinkBuilder: TextGiftLinkBuilder
) : CoroutineScope by coroutineScope {
    @AssistedFactory
    interface Factory {
        fun create(navParam: WrappingNavParam, coroutineScope: CoroutineScope): WrappingViewModel
    }

    enum class VisualState {
        DEFAULT,
        IN_PROGRESS,
    }

    private val stateMutable = BehaviorSubject.create<VisualState>()
    private val _senderRelay = BehaviorRelay.createDefault("")
    private val _receiverRelay = BehaviorRelay.createDefault("")
    private val _giftContentRelay = BehaviorRelay.createDefault("")

    private val _shareGiftLinkSubject = PublishSubject.create<String>()
    private val _showErrorSubject = PublishSubject.create<String>()

    private val _originalGift = navParam.existingGiftUuid?.let {
        giftRepository.findGift(it)
    }

    private val _isSentSubject =
        BehaviorSubject.createDefault(_originalGift?.type == GiftType.Sent)

    init {
        _originalGift?.let {
            _senderRelay.accept(it.sender)
            _receiverRelay.accept(it.receiver)
            _giftContentRelay.accept(it.text)
        }
    }

    val state: Observable<VisualState> = stateMutable
    val editingEnabled: Observable<Boolean> = _isSentSubject.map { !it }
    val sendButtonVisible: Observable<Boolean> = _isSentSubject.map { !it }
    val sentLabelVisible: Observable<Boolean> = _isSentSubject
    val sender: Observable<String> = _senderRelay.distinctUntilChanged()
    val receiver: Observable<String> = _receiverRelay.distinctUntilChanged()
    val giftContent: Observable<String> = _giftContentRelay.distinctUntilChanged()
    val sentDate: String = java.text.SimpleDateFormat.getDateInstance().format(_originalGift?.date ?: Date())

    val showErrorCommand: Observable<String> = _showErrorSubject
    val shareGiftLinkCommand: Observable<String> = _shareGiftLinkSubject

    val senderInput: Consumer<String> = _senderRelay
    val receiverInput: Consumer<String> = _receiverRelay
    val giftContentInput: Consumer<String> = _giftContentRelay

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
                _isSentSubject.value ?: false -> ExitMode.NoChanges
                newGift && !emptyContent -> ExitMode.New
                !newGift && emptyContent -> ExitMode.Cleaned
                originalGiftChanged -> ExitMode.Edited
                else -> ExitMode.NoChanges
            }
        }

    fun onSendGiftClick() {
        if (debounce) return
        sendGift()
    }

    fun onSaveGiftClick() {
        if (debounce) return
        saveGift()
    }

    fun onDeleteGiftClick() {
        if (debounce) return
        deleteGift()
    }

    private fun sendGift() {
        val gift = dumpGift().copy(type = GiftType.Sent)

        if (_senderRelay.value.isNullOrBlank()
            || _receiverRelay.value.isNullOrBlank()
            || _giftContentRelay.value.isNullOrBlank()
        ) {
            _showErrorSubject.onNext("Cannot send unfinished gift. Please fill every field.")
            return
        }

        launch {
            stateMutable.onNext(VisualState.IN_PROGRESS)
            val link = giftLinkBuilder.build(gift)
            stateMutable.onNext(VisualState.DEFAULT)

            if (link == null) {
                _showErrorSubject.onNext("Failed to prepare a link. Please try again.")
                return@launch
            }

            saveGift(gift)
            _shareGiftLinkSubject.onNext(link)
            _isSentSubject.onNext(true)
        }

    }

    private fun saveGift(gift: TextGift? = null) {
        giftRepository.saveTextGift(
            gift ?: dumpGift()
        )
    }

    private fun dumpGift() = TextGift(
        _originalGift?.uuid ?: UUID.randomUUID().toString(),
        _senderRelay.value ?: "",
        _receiverRelay.value ?: "",
        _originalGift?.date ?: Date(),
        _originalGift?.type ?: GiftType.Created,
        _giftContentRelay.value ?: ""
    )

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
