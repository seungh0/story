package com.story.core.domain.feed

import com.story.core.domain.event.EventRecord
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class FeedCreator(
    private val feedWriteRepository: FeedWriteRepository,
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
                        feedWriteRepository.create(
                            workspaceId = payload.workspaceId,
                            feedComponentId = payload.feedComponentId,
                            slotId = payload.slotId,
                            eventKey = event.eventKey,
                            feedId = event.eventId,
                            sourceComponentId = payload.sourceComponentId,
                            sourceResourceId = payload.sourceResourceId,
                            subscriberIds = chunkedSubscriberIds.toSet(),
                            retention = payload.retention,
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
