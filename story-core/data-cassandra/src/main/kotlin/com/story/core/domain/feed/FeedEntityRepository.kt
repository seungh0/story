package com.story.core.domain.feed

import com.story.core.support.cassandra.executeCoroutine
import com.story.core.support.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class FeedEntityRepository(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val feedEntityCassandraRepository: FeedEntityCassandraRepository,
    private val feedReverseEntityCassandraRepository: FeedReverseEntityCassandraRepository,
) : FeedWriteRepository, FeedReadRepository {

    override suspend fun create(
        workspaceId: String,
        componentId: String,
        ownerIds: Collection<String>,
        priority: Long,
        item: FeedItem,
        options: FeedOptions,
    ) {
        val feeds = ownerIds.map { ownerId ->
            FeedEntity(
                key = FeedEntityPrimaryKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    ownerId = ownerId,
                    priority = priority,
                    channelId = item.channelId,
                    itemResourceId = item.resourceId,
                    itemComponentId = item.componentId,
                    itemId = item.itemId,
                ),
                createdAt = LocalDateTime.now(),
            )
        }

        val feedReverses = feeds.map { feed -> FeedReverseEntity.from(feed) }

        reactiveCassandraOperations.batchOps()
            .upsert(entities = feeds, ttl = options.retention)
            .upsert(entities = feedReverses, ttl = options.retention)
            .executeCoroutine()
    }

    override suspend fun create(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        items: List<FeedItemWithOption>,
        options: FeedOptions,
    ) {
        val feeds = items.map { itemWithOption ->
            FeedEntity(
                key = FeedEntityPrimaryKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    ownerId = ownerId,
                    priority = itemWithOption.priority,
                    channelId = itemWithOption.item.channelId,
                    itemResourceId = itemWithOption.item.resourceId,
                    itemComponentId = itemWithOption.item.componentId,
                    itemId = itemWithOption.item.itemId,
                ),
                createdAt = LocalDateTime.now(),
            )
        }

        val feedReverses = feeds.map { feed -> FeedReverseEntity.from(feed) }

        reactiveCassandraOperations.batchOps()
            .upsert(entities = feeds, ttl = options.retention)
            .upsert(entities = feedReverses, ttl = options.retention)
            .executeCoroutine()
    }

    override suspend fun delete(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        item: FeedItem,
    ) {
        val feedReverse = feedReverseEntityCassandraRepository.findById(
            FeedReverseEntityPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                ownerId = ownerId,
                itemResourceId = item.resourceId,
                itemComponentId = item.componentId,
                channelId = item.channelId,
                itemId = item.itemId,
            )
        )

        if (feedReverse == null) {
            return
        }

        reactiveCassandraOperations.batchOps()
            .delete(feedReverse)
            .delete(
                FeedEntity(
                    key = FeedEntityPrimaryKey(
                        workspaceId = feedReverse.key.workspaceId,
                        componentId = feedReverse.key.componentId,
                        ownerId = feedReverse.key.ownerId,
                        channelId = item.channelId,
                        priority = feedReverse.priority,
                        itemResourceId = feedReverse.key.itemResourceId,
                        itemComponentId = feedReverse.key.itemComponentId,
                        itemId = feedReverse.key.itemId,
                    ),
                    createdAt = LocalDateTime.now(), // Dummy
                )
            )
            .executeCoroutine()
    }

    override suspend fun clearByChannel(workspaceId: String, componentId: String, ownerId: String, channelId: String) {
        var pageable: Pageable = CassandraPageRequest.first(100)
        do {
            val feedReverses = feedReverseEntityCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeyChannelId(
                workspaceId = workspaceId,
                componentId = componentId,
                ownerId = ownerId,
                channelId = channelId,
                pageable = pageable,
            )

            if (feedReverses.hasContent()) {
                reactiveCassandraOperations.batchOps()
                    .delete(feedReverses.content)
                    .delete(feedReverses.content.map { feedReverse -> FeedEntity.from(feedReverse) })
                    .executeCoroutine()
            }

            if (feedReverses.hasNext()) {
                pageable = feedReverses.nextPageable()
            }

        } while (feedReverses.hasNext())
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerId(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        pageable: Pageable,
    ): Slice<Feed> {
        return feedEntityCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerId(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            pageable = pageable,
        ).map { entity -> entity.toFeed() }
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeyPriorityLessThan(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        priority: Long,
        pageable: Pageable,
    ): Slice<Feed> {
        return feedEntityCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeyPriorityLessThan(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            priority = priority,
            pageable = pageable,
        ).map { entity -> entity.toFeed() }
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdOrderByKeyPriortyAsc(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        pageable: Pageable,
    ): Slice<Feed> {
        return feedEntityCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdOrderByKeyPriorityAsc(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            pageable = pageable,
        ).map { entity -> entity.toFeed() }
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeyPriorityGreaterThanOrderByKeyPriorityAsc(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        priority: Long,
        pageable: Pageable,
    ): Slice<Feed> {
        return feedEntityCassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeyPriorityGreaterThanOrderByKeyPriorityAsc(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            priority = priority,
            pageable = pageable,
        ).map { entity -> entity.toFeed() }
    }

}
