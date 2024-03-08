package com.story.core.domain.feed

import com.story.core.domain.event.EventRecord
import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.cassandra.upsert
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class FeedCreator(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun createFeeds(
        event: EventRecord<*>,
        payload: FeedFanoutMessage,
        subscriberIds: Collection<String>,
        parallelCount: Int = 50,
    ) = coroutineScope {
        subscriberIds.asSequence()
            .chunked(BATCH_SIZE)
            .chunked(parallelCount)
            .map { parallelChunkedSubscriberIds ->
                launch {
                    parallelChunkedSubscriberIds.map { chunkedSubscriberIds ->
                        val feedSubscribers = chunkedSubscriberIds.map { subscriberId ->
                            FeedSubscriber(
                                key = FeedSubscriberPrimaryKey.of(
                                    workspaceId = payload.workspaceId,
                                    feedComponentId = payload.feedComponentId,
                                    slotId = payload.slotId,
                                    subscriberId = subscriberId,
                                    eventKey = event.eventKey,
                                ),
                                feedId = event.eventId,
                                sourceComponentId = payload.sourceComponentId,
                                sourceResourceId = payload.sourceResourceId,
                            )
                        }
                        val feeds = feedSubscribers.map { feedSubscriber -> Feed.from(feedSubscriber) }

                        reactiveCassandraOperations.batchOps()
                            .upsert(entities = feedSubscribers, ttl = payload.retention)
                            .upsert(entities = feeds, ttl = payload.retention)
                            .executeCoroutine()
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
