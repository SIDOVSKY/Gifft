package com.gifft.gift

import android.net.Uri
import com.gifft.gift.api.GiftType
import com.gifft.gift.api.TextGift
import com.gifft.gift.api.TextGiftLinkBuilder
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class TextGiftLinkBuilderImpl @Inject constructor() : TextGiftLinkBuilder {
    companion object {
        private const val DELIMITER = "|"
    }

    override suspend fun build(gift: TextGift): String? = gift.run {
        val giftString = sequence {
            yield(uuid)
            yield(sender)
            yield(receiver)
            yield(text)
        }.joinToString(DELIMITER)

        return try {
            val linkTask = Firebase.dynamicLinks.shortLinkAsync {
                link = Uri.parse("https://github.com/SIDOVSKY/Gifft/?gift=$giftString")
                domainUriPrefix = "https://${BuildConfig.APP_DEEP_LINK_HOST}"
                androidParameters("com.gifft") {
                    fallbackUrl = Uri.parse("https://github.com/SIDOVSKY/Gifft/")
                    minimumVersion = 125
                }
                socialMetaTagParameters {
                    title = "Gifft!"
                    description = "Gift from ${gift.sender}"
                }
            }.await()

            linkTask.shortLink?.toString()
        } catch (ex: Exception) {
            null
        }
    }

    override suspend fun parse(encodedGift: Uri): TextGift? {
        return try {
            val linkTask = Firebase.dynamicLinks.getDynamicLink(encodedGift).await()
            val giftString = linkTask.link?.getQueryParameter("gift")
                ?: return null

            val items = giftString.split(DELIMITER)

            if (items.size != 4)
                return null

            return TextGift(
                uuid = items[0],
                sender = items[1],
                receiver = items[2],
                date = Date(),
                text = items[3],
                type = GiftType.Unknown
            )
        } catch (ex: Exception) {
            null
        }
    }
}
