package com.gifft.gift

import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.GiftType
import com.gifft.gift.api.TextGift
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.flow
import io.objectbox.kotlin.query
import io.objectbox.query.QueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GiftRepositoryImpl @Inject constructor(
    boxStore: BoxStore
) : GiftRepository {

    private val textGiftBox = boxStore.boxFor<TextGiftEntity>()

    override fun allReceivedGifts(): Flow<List<TextGift>> {
        return giftsOfTypes(GiftType.Received)
    }

    override fun allCreatedGifts(): Flow<List<TextGift>> {
        return giftsOfTypes(GiftType.Created, GiftType.Sent)
    }

    override fun saveTextGift(gift: TextGift) {
        val existingEntity = findGiftEntity(gift.uuid)

        textGiftBox.put(
            TextGiftEntity(
                existingEntity?.id ?: 0,
                gift.uuid,
                gift.text,
                gift.date,
                gift.sender,
                gift.receiver,
                gift.type
            )
        )
    }

    override fun findGift(uuid: String): TextGift? =
        findGiftEntity(uuid)?.toTextGift()

    override fun deleteGift(uuid: String) {
        textGiftBox
            .query {
                equal(TextGiftEntity_.uuid, uuid, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            }
            .remove()
    }

    private fun findGiftEntity(uuid: String): TextGiftEntity? =
        textGiftBox
            .query {
                equal(TextGiftEntity_.uuid, uuid, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            }
            .find()
            .firstOrNull()

    private fun giftsOfTypes(vararg acceptableTypes: GiftType): Flow<List<TextGift>> =
        textGiftBox
            .query {
                filter { acceptableTypes.contains(it.type) }
                orderDesc(TextGiftEntity_.date)
            }
            .flow()
            .map { list ->
                list.map { it.toTextGift() }
            }
}
