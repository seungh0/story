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
        payload: FeedEvent,
        subscriberIds: Collection<String>,
        parallelCount: Int = 50,
    ) = coroutineScope {
        subscriberIds.chunked(parallelCount).forEach { chunkedSubscriberIds ->
            chunkedSubscriberIds.map { subscriberId ->
                launch {
                    val feedSubscriber = FeedSubscriber(
                        key = FeedSubscriberPrimaryKey.of(
                            workspaceId = payload.workspaceId,
                            feedComponentId = payload.feedComponentId,
                            slotId = payload.slotId,
                            subscriberId = subscriberId,
                            eventKey = event.eventKey,
                        ),
                        feedId = event.eventId,
                    )
                    val feed = Feed(
                        key = FeedPrimaryKey.of(
                            workspaceId = payload.workspaceId,
                            feedComponentId = payload.feedComponentId,
                            subscriberId = subscriberId,
                            feedId = event.eventId,
                        ),
                        sourceResourceId = payload.sourceResourceId,
                        sourceComponentId = payload.sourceComponentId,
                        eventKey = event.eventKey,
                        subscriberSlot = payload.slotId,
                    )

                    reactiveCassandraOperations.batchOps()
                        .upsert(feedSubscriber)
                        .upsert(feed)
                        .executeCoroutine()
                }
            }.joinAll()
        }
    }

}
