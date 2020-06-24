package com.gifft.gift

import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.TextGift
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.objectbox.rx.RxQuery
import io.reactivex.Observable
import javax.inject.Inject

internal class GiftRepositoryImpl @Inject constructor(
    boxStore: BoxStore
) : GiftRepository {

    private val textGiftBox = boxStore.boxFor<TextGiftEntity>()

    override fun allReceivedGifts(): Observable<List<TextGift>> {
        return giftsOfType(GiftType.Received)
    }

    override fun allCreatedGifts(): Observable<List<TextGift>> {
        return giftsOfType(GiftType.Created)
    }

    override fun saveCreatedTextGift(gift: TextGift) {
        val existingEntity = findGiftEntity(gift.uuid)

        textGiftBox.put(
            TextGiftEntity(
                existingEntity?.id ?: 0,
                gift.uuid,
                gift.text,
                gift.date,
                gift.sender,
                gift.receiver,
                GiftType.Created
            )
        )
    }

    override fun findGift(uuid: String): TextGift? =
        findGiftEntity(uuid)?.toTextGift()

    override fun deleteGift(uuid: String) {
        textGiftBox
            .query {
                equal(TextGiftEntity_.uuid, uuid)
            }
            .remove()
    }

    private fun findGiftEntity(uuid: String): TextGiftEntity? =
        textGiftBox
            .query {
                equal(TextGiftEntity_.uuid, uuid)
            }
            .find()
            .firstOrNull()

    private fun giftsOfType(type: GiftType): Observable<List<TextGift>> =
        RxQuery
            .observable(
                textGiftBox.query {
                    filter { it.type == type }
                    orderDesc(TextGiftEntity_.date)
                })
            .map { list ->
                list.map { it.toTextGift() }
            }
}
