package com.story.core.domain.feed

import com.story.core.domain.resource.ResourceId

data class FeedId(
    val itemResourceId: ResourceId,
    val itemComponentId: String,
    val itemId: String,
) {

    fun makeKey() = itemResourceId.code + ":" + itemComponentId + ":" + itemId

    fun toItem() = FeedItem(
        itemId = itemId,
        componentId = itemComponentId,
        resourceId = itemResourceId,
    )

    companion object {
        fun of(feedId: String): FeedId {
            return FeedId(
                ResourceId.findByCode(feedId.split(":")[0]),
                feedId.split(":")[1],
                feedId.split(":")[2],
            )
        }
    }

}
