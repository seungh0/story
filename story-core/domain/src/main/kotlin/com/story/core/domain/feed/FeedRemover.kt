package com.story.core.domain.feed

import org.springframework.stereotype.Service

@Service
class FeedRemover(
    private val feedWriteRepository: FeedWriteRepository,
) {

    suspend fun remove(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        item: FeedItem,
    ) {
        feedWriteRepository.delete(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            item = item,
        )
    }

    suspend fun clearByChannel(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        channelId: String,
    ) {
        feedWriteRepository.clearByChannel(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            channelId = channelId,
        )
    }

}
