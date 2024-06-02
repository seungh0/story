package com.story.core.domain.feed

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class FeedRemover(
    private val feedWriteRepository: FeedWriteRepository,
) {

    suspend fun remove(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        feedId: Long,
    ) {
        feedWriteRepository.delete(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            subscriberId = subscriberId,
            feedId = feedId,
        )
    }

    suspend fun remove(
        workspaceId: String,
        feedComponentId: String,
        feedSubscribers: Collection<Feed>,
        parallelCount: Int = 50,
    ) = coroutineScope {
        feedSubscribers.chunked(10)
            .chunked(parallelCount)
            .map { parallelChunkedFeedSubscribers ->
                parallelChunkedFeedSubscribers.map { feedSubscribers ->
                    launch {
                        feedWriteRepository.delete(
                            workspaceId = workspaceId,
                            feedComponentId = feedComponentId,
                            feedSubscribers = feedSubscribers,
                        )
                    }
                }.joinAll()
            }
    }

}
