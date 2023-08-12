package com.story.platform.executor.domain.feed

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.feed.Feed
import com.story.platform.core.domain.feed.FeedEvent
import com.story.platform.core.domain.feed.FeedPrimaryKey
import com.story.platform.core.domain.feed.FeedReactiveRepository
import com.story.platform.core.domain.feed.FeedSubscriber
import com.story.platform.core.domain.feed.FeedSubscriberPrimaryKey
import com.story.platform.core.domain.feed.FeedSubscriberRepository
import com.story.platform.core.domain.subscription.SubscriberRepository
import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable

// TODO: 인터페이스로 구조화하기
@HandlerAdapter
class FeedPublishHandler(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val subscriberRepository: SubscriberRepository,
    private val feedSubscriberRepository: FeedSubscriberRepository,
    private val feedReactiveRepository: FeedReactiveRepository,
) {

    suspend fun publishFeeds(event: EventRecord<*>, payload: FeedEvent) = coroutineScope {
        when (event.eventAction) {
            EventAction.CREATED -> {
                var pageable: Pageable = CassandraPageRequest.first(500)
                do {
                    val subscribers = subscriberRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotId(
                        workspaceId = payload.workspaceId,
                        componentId = payload.subscriptionComponentId,
                        targetId = payload.targetId,
                        slotId = payload.slotId,
                        pageable = pageable
                    )

                    subscribers.chunked(50).forEach { chunkedSubscribers ->
                        chunkedSubscribers.map { subscriber ->
                            launch {
                                val feedSubscriber = FeedSubscriber(
                                    key = FeedSubscriberPrimaryKey.of(
                                        workspaceId = payload.workspaceId,
                                        feedComponentId = payload.feedComponentId,
                                        slotId = payload.slotId,
                                        subscriberId = subscriber.key.subscriberId,
                                        eventKey = event.eventKey,
                                    ),
                                    eventId = event.eventId
                                )
                                val feed = Feed(
                                    key = FeedPrimaryKey.of(
                                        workspaceId = payload.workspaceId,
                                        feedComponentId = payload.feedComponentId,
                                        subscriberId = subscriber.key.subscriberId,
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
                    pageable = subscribers.nextPageable()
                } while (subscribers.hasNext())
            }

            EventAction.UPDATED -> {
                var pageable: Pageable = CassandraPageRequest.first(500)
                do {
                    val feedSubscribers = feedSubscriberRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotId(
                        workspaceId = payload.workspaceId,
                        feedComponentId = payload.feedComponentId,
                        slotId = payload.slotId,
                        pageable = pageable,
                        eventKey = event.eventKey,
                    )

                    val feeds = feedSubscribers.content.map { feedSubscriber ->
                        Feed(
                            key = FeedPrimaryKey.of(
                                workspaceId = feedSubscriber.key.workspaceId,
                                feedComponentId = feedSubscriber.key.feedComponentId,
                                subscriberId = feedSubscriber.key.subscriberId,
                                eventId = feedSubscriber.eventId,
                            ),
                            sourceResourceId = payload.sourceResourceId,
                            sourceComponentId = payload.sourceComponentId,
                            payloadJson = payload.payloadJson,
                        )
                    }

                    feedReactiveRepository.insert(feeds).awaitFirstOrNull()

                    pageable = feedSubscribers.nextPageable()
                } while (feedSubscribers.hasNext())
            }

            EventAction.DELETED -> {
                var pageable: Pageable = CassandraPageRequest.first(500)
                do {
                    val feedSubscribers = feedSubscriberRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotId(
                        workspaceId = payload.workspaceId,
                        feedComponentId = payload.feedComponentId,
                        eventKey = event.eventKey,
                        slotId = payload.slotId,
                        pageable = pageable,
                    )

                    feedSubscribers.content.chunked(50).forEach { chunkedFeedSubscribers ->
                        chunkedFeedSubscribers.map { feedSubscriber ->
                            launch {
                                val feed = Feed(
                                    key = FeedPrimaryKey.of(
                                        workspaceId = feedSubscriber.key.workspaceId,
                                        feedComponentId = feedSubscriber.key.feedComponentId,
                                        subscriberId = feedSubscriber.key.subscriberId,
                                        eventId = feedSubscriber.eventId,
                                    ),
                                    sourceResourceId = payload.sourceResourceId,
                                    sourceComponentId = payload.sourceComponentId,
                                    payloadJson = payload.payloadJson,
                                )

                                reactiveCassandraOperations.batchOps()
                                    .delete(feedSubscriber)
                                    .delete(feed)
                                    .executeCoroutine()
                            }
                        }.joinAll()
                    }
                    pageable = feedSubscribers.nextPageable()
                } while (feedSubscribers.hasNext())
            }
        }

    }

}
