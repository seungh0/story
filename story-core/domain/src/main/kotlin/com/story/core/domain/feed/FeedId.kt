package com.story.core.domain.feed

import com.story.core.domain.resource.ResourceId
import java.util.Base64
import java.util.StringJoiner

data class FeedId(
    val itemResourceId: ResourceId,
    val itemComponentId: String,
    val channelId: String,
    val itemId: String,
) {

    fun makeKey(): String = encoder.encodeToString(
        StringJoiner(DELIMITER)
            .add(itemResourceId.code)
            .add(itemComponentId)
            .add(channelId)
            .add(itemId)
            .toString()
            .toByteArray()
    )

    fun toItem() = FeedItem(
        itemId = itemId,
        componentId = itemComponentId,
        resourceId = itemResourceId,
        channelId = channelId,
    )

    companion object {
        private val encoder = Base64.getUrlEncoder()
        private val decoder = Base64.getUrlDecoder()
        private const val DELIMITER = ":"

        fun of(feedId: String): FeedId {
            val elements = String(decoder.decode(feedId)).split(DELIMITER)
            return FeedId(
                ResourceId.findByCode(elements[0]),
                elements[1],
                elements[2],
                elements[3],
            )
        }
    }

}
