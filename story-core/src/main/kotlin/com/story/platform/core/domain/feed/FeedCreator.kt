package com.story.platform.core.domain.feed

import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
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
    ) = coroutineScope {
        subscriberIds.chunked(50).forEach { chunkedSubscriberIds ->
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
                        eventId = event.eventId,
                    )
                    val feed = Feed(
                        key = FeedPrimaryKey.of(
                            workspaceId = payload.workspaceId,
                            feedComponentId = payload.feedComponentId,
                            subscriberId = subscriberId,
                            eventId = event.eventId,
                        ),
                        sourceResourceId = payload.sourceResourceId,
                        sourceComponentId = payload.sourceComponentId,
                        payloadJson = payload.payloadJson,
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
