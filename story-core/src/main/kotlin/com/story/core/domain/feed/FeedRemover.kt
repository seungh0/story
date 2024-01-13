package com.story.core.domain.feed

import com.story.core.infrastructure.cassandra.executeCoroutine
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class FeedRemover(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val feedRepository: FeedRepository,
) {

    suspend fun remove(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        feedId: Long,
    ) {
        val feed = feedRepository.findById(
            FeedPrimaryKey.of(
                workspaceId = workspaceId,
                feedComponentId = componentId,
                subscriberId = subscriberId,
                feedId = feedId,
            )
        ) ?: return

        reactiveCassandraOperations.batchOps()
            .delete(feed)
            .delete(FeedSubscriber.from(feed))
            .executeCoroutine()
    }

    suspend fun remove(
        feedSubscribers: Collection<FeedSubscriber>,
        parallelCount: Int = 50,
    ) = coroutineScope {
        feedSubscribers.chunked(10).chunked(parallelCount).map { parallelChunkedFeedSubscribers ->
            parallelChunkedFeedSubscribers.map { feedSubscribers ->
                val feeds = feedSubscribers.map { feedSubscriber -> Feed.from(feedSubscriber = feedSubscriber) }
                launch {
                    reactiveCassandraOperations.batchOps()
                        .delete(feeds)
                        .delete(feedSubscribers)
                        .executeCoroutine()
                }
            }.joinAll()
        }
    }

}
