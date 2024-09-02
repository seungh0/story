package com.story.core.domain.feed

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class FeedCreator(
    private val feedWriteRepository: FeedWriteRepository,
) {

    suspend fun create(
        workspaceId: String,
        componentId: String,
        ownerIds: Collection<String>,
        sortKey: Long,
        item: FeedItem,
        options: FeedOptions,
        parallelCount: Int = 50,
    ) = coroutineScope {
        ownerIds.asSequence()
            .chunked(BATCH_SIZE)
            .chunked(parallelCount)
            .map { parallelChunkedOwnerIds ->
                launch {
                    parallelChunkedOwnerIds.map { chunkedOwnerIds ->
                        feedWriteRepository.create(
                            workspaceId = workspaceId,
                            componentId = componentId,
                            ownerIds = chunkedOwnerIds,
                            sortKey = sortKey,
                            item = item,
                            options = options,
                        )
                    }
                }
            }
            .toList()
            .joinAll()
    }

    companion object {
        private const val BATCH_SIZE = 10 // 10 * 200byte = 2KB
    }

}
