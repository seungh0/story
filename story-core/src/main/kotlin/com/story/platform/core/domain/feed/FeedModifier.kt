package com.story.platform.core.domain.feed

import com.story.platform.core.domain.event.EventRecord
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Service

@Service
class FeedModifier(
    private val feedReactiveRepository: FeedReactiveRepository,
) {

    suspend fun modify(
        event: EventRecord<*>,
        payload: FeedEvent,
        subscriberIds: Collection<String>,
    ) {
        val feeds = subscriberIds.map { subscriberId ->
            Feed(
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
        }

        feedReactiveRepository.insert(feeds).awaitFirstOrNull()
    }

}
