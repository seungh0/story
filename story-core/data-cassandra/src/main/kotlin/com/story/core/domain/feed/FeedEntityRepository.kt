package com.story.core.domain.feed

import com.story.core.common.utils.mapToSet
import com.story.core.domain.resource.ResourceId
import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class FeedEntityRepository(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val feedCassandraRepository: FeedCassandraRepository,
    private val feedSubscriberCassandraRepository: FeedSubscriberCassandraRepository,
) : FeedWriteRepository, FeedReadRepository {

    override suspend fun create(
        workspaceId: String,
        feedComponentId: String,
        slotId: Long,
        subscriberIds: Collection<String>,
        eventKey: String,
        retention: Duration,
        feedId: Long,
        sourceComponentId: String,
        sourceResourceId: ResourceId,
    ) {
        val feedSubscribers: Set<FeedSubscriberEntity> = subscriberIds.mapToSet { subscriberId ->
            FeedSubscriberEntity(
                key = FeedSubscriberPrimaryKey.of(
                    workspaceId = workspaceId,
                    feedComponentId = feedComponentId,
                    slotId = slotId,
                    subscriberId = subscriberId,
                    eventKey = eventKey,
                ),
                feedId = feedId,
                sourceComponentId = sourceComponentId,
                sourceResourceId = sourceResourceId,
            )
        }

        val feeds: Set<FeedEntity> = feedSubscribers.mapToSet { feedSubscriber -> FeedEntity.from(feedSubscriber) }

        reactiveCassandraOperations.batchOps()
            .upsert(entities = feeds, ttl = retention)
            .upsert(entities = feedSubscribers, ttl = retention)
            .executeCoroutine()
    }

    override suspend fun delete(workspaceId: String, feedComponentId: String, subscriberId: String, feedId: Long) {
        val feed = feedCassandraRepository.findById(
            FeedPrimaryKey.of(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                subscriberId = subscriberId,
                feedId = feedId,
            )
        )
            ?: throw FeedNotExistsExeption("워크스페이스($workspaceId)의 피드 컴포넌트($feedComponentId)상에서 피드 구독자($subscriberId)에게 존재하지 않는 피드($feedId)입니다")

        reactiveCassandraOperations.batchOps()
            .delete(feed)
            .delete(FeedSubscriberEntity.from(feed))
            .executeCoroutine()
    }

    override suspend fun delete(workspaceId: String, feedComponentId: String, feedSubscribers: Collection<Feed>) {
        val feeds = feedSubscribers.map { feedSubscriber ->
            FeedEntity(
                key = FeedPrimaryKey(
                    workspaceId = workspaceId,
                    feedComponentId = feedComponentId,
                    subscriberId = feedSubscriber.subscriberId,
                    feedId = feedSubscriber.feedId,
                ),
                sourceResourceId = feedSubscriber.sourceResourceId,
                sourceComponentId = feedSubscriber.sourceComponentId,
                eventKey = "", // Dummy
                subscriberSlot = 0L, // Dummy
            )
        }

        reactiveCassandraOperations.batchOps()
            .delete(feeds)
            .delete(feedSubscribers)
            .executeCoroutine()
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberId(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Feed> {
        val feeds = feedCassandraRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberId(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            subscriberId = subscriberId,
            pageable = pageable,
        )

        return SliceImpl(feeds.content.map { entity -> entity.toFeed() }, feeds.nextPageable(), feeds.hasNext())
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdLessThan(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        feedId: Long,
        pageable: Pageable,
    ): Slice<Feed> {
        val feeds = feedCassandraRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdLessThan(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            subscriberId = subscriberId,
            feedId = feedId,
            pageable = pageable,
        )
        return SliceImpl(feeds.content.map { entity -> entity.toFeed() }, feeds.nextPageable(), feeds.hasNext())
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdOrderByKeyFeedIdAsc(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Feed> {
        val feeds = feedCassandraRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdOrderByKeyFeedIdAsc(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            subscriberId = subscriberId,
            pageable = pageable,
        )
        return SliceImpl(feeds.content.map { entity -> entity.toFeed() }, feeds.nextPageable(), feeds.hasNext())
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdGreaterThanOrderByKeyFeedIdAsc(
        workspaceId: String,
        feedComponentId: String,
        subscriberId: String,
        feedId: Long,
        pageable: Pageable,
    ): Slice<Feed> {
        val feeds = feedCassandraRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySubscriberIdAndKeyFeedIdGreaterThanOrderByKeyFeedIdAsc(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            subscriberId = subscriberId,
            feedId = feedId,
            pageable = pageable,
        )
        return SliceImpl(feeds.content.map { entity -> entity.toFeed() }, feeds.nextPageable(), feeds.hasNext())
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotId(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
        pageable: Pageable,
    ): Slice<Feed> {
        val feedSubscribers = feedSubscriberCassandraRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotId(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            eventKey = eventKey,
            slotId = slotId,
            pageable = pageable,
        )
        return SliceImpl(
            feedSubscribers.content.map { entity -> entity.toFeed() },
            feedSubscribers.nextPageable(),
            feedSubscribers.hasNext()
        )
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotIdAndKeySubscriberIdLessThan(
        workspaceId: String,
        feedComponentId: String,
        eventKey: String,
        slotId: Long,
        subscriberId: String,
        pageable: Pageable,
    ): Slice<Feed> {
        val feedSubscribers = feedSubscriberCassandraRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeyEventKeyAndKeySlotIdAndKeySubscriberIdLessThan(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            eventKey = eventKey,
            slotId = slotId,
            subscriberId = subscriberId,
            pageable = pageable,
        )
        return SliceImpl(
            feedSubscribers.content.map { entity -> entity.toFeed() },
            feedSubscribers.nextPageable(),
            feedSubscribers.hasNext()
        )
    }

}
