package com.story.platform.core.domain.feed

import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class FeedRemover(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun remove(
        event: EventRecord<*>,
        payload: FeedEvent,
        subscriberIds: Collection<String>,
    ) = coroutineScope {
        subscriberIds.chunked(50).forEach { chunkedSubscriberIds ->
            chunkedSubscriberIds.map { subscriberId ->
                launch {
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
                        eventKey = event.eventKey,
                    )
                    reactiveCassandraOperations.batchOps()
                        .delete(feed)
                        .delete(
                            FeedSubscriber(
                                key = FeedSubscriberPrimaryKey(
                                    workspaceId = payload.workspaceId,
                                    feedComponentId = payload.feedComponentId,
                                    eventKey = event.eventKey,
                                    slotId = payload.slotId,
                                    subscriberId = subscriberId,
                                ),
                                feedId = event.eventId,
                            )
                        )
                        .executeCoroutine()
                }
            }.joinAll()
        }
    }

}
