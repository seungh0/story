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
        )
            ?: throw FeedNotExistsExeption("워크스페이스($workspaceId)의 피드 컴포넌트($componentId)상에서 피드 구독자($subscriberId)에게 존재하지 않는 피드($feedId)입니다")

        reactiveCassandraOperations.batchOps()
            .delete(feed)
            .delete(FeedSubscriberEntity.from(feed))
            .executeCoroutine()
    }

    suspend fun remove(
        workspaceId: String,
        componentId: String,
        feedSubscribers: Collection<Feed>,
        parallelCount: Int = 50,
    ) = coroutineScope {
        feedSubscribers.chunked(10).chunked(parallelCount).map { parallelChunkedFeedSubscribers ->
            parallelChunkedFeedSubscribers.map { feedSubscribers ->
                val feeds = feedSubscribers.map { feedSubscriber ->
                    FeedEntity(
                        key = FeedPrimaryKey(
                            workspaceId = workspaceId,
                            feedComponentId = componentId,
                            subscriberId = feedSubscriber.subscriberId,
                            feedId = feedSubscriber.feedId,
                        ),
                        sourceResourceId = feedSubscriber.sourceResourceId,
                        sourceComponentId = feedSubscriber.sourceComponentId,
                        eventKey = "", // Dummy
                        subscriberSlot = 0L, // Dummy
                    )
                }
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
