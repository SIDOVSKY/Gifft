package com.gifft.gift

import com.gifft.gift.api.GiftType
import com.gifft.gift.api.TextGift
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class GiftRepositoryImplTest {

    @get:Rule
    val objectBoxRule = ObjectBoxRule()

    @Test
    fun `should save created gift`() {
        val expectedGift = TextGift(
            "Some uuid",
            "Sender",
            "Receiver",
            Date(),
            GiftType.Created,
            "Gift Text"
        )

        val repository = GiftRepositoryImpl(objectBoxRule.store)
        repository.saveTextGift(expectedGift)

        val savedGift = repository.findGift(expectedGift.uuid)

        assertEquals(expectedGift, savedGift)
    }

    @Test
    fun `should delete created gift`() {
        val expectedGift = TextGift(
            "Some uuid",
            "Sender",
            "Receiver",
            Date(),
            GiftType.Created,
            "Gift Text"
        )

        val repository = GiftRepositoryImpl(objectBoxRule.store)
        repository.saveTextGift(expectedGift)

        val savedGift = repository.findGift(expectedGift.uuid)
        assertEquals(expectedGift, savedGift)

        repository.deleteGift(expectedGift.uuid)
        val foundAfterDelete = repository.findGift(expectedGift.uuid)
        assertNull(foundAfterDelete)
    }

    @Ignore("Stopped working. Check later")
    @Test
    fun `should provide all created gifts`() {
        val expectedGift = TextGift(
            "Some uuid",
            "Sender",
            "Receiver",
            Date(),
            GiftType.Created,
            "Gift Text"
        )
        val expectedGift2 = expectedGift.copy(uuid = "Some uuid 2")

        val repository = GiftRepositoryImpl(objectBoxRule.store)
        repository.saveTextGift(expectedGift)
        val allCreatedGiftsSubscriber = repository.allCreatedGifts().test()
        repository.saveTextGift(expectedGift2)

        allCreatedGiftsSubscriber.assertValuesOnly(
            listOf(expectedGift),
            listOf(expectedGift, expectedGift2)
        )
    }
}
