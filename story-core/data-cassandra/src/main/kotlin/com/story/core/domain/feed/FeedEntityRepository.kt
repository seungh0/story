package com.story.core.domain.feed

import com.story.core.support.cassandra.executeCoroutine
import com.story.core.support.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class FeedEntityRepository(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val feedEntityV2CassandraRepository: FeedEntityV2CassandraRepository,
) : FeedWriteRepository, FeedReadRepository {

    override suspend fun create(
        workspaceId: String,
        componentId: String,
        ownerIds: Collection<String>,
        sortKey: Long,
        item: FeedItem,
        options: FeedOptions,
    ) {
        val feeds = ownerIds.map { ownerId ->
            FeedEntity(
                key = FeedEntityPrimaryKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    ownerId = ownerId,
                    sortKey = sortKey,
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

    override suspend fun delete(
        workspaceId: String,
        componentId: String,
        ownerIds: Collection<String>,
        item: FeedItem,
        options: FeedOptions,
    ) {
        val feedDeletes = ownerIds.map { ownerId ->
            FeedDeletedEntity(
                key = FeedDeletedEntityPrimaryKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    ownerId = ownerId,
                    itemResourceId = item.resourceId,
                    itemComponentId = item.componentId,
                    itemId = item.itemId,
                ),
                deletedAt = LocalDateTime.now(),
            )
        }
        reactiveCassandraOperations.batchOps()
            .upsert(entities = feedDeletes, ttl = options.retention)
            .executeCoroutine()
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerId(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        pageable: Pageable,
    ): Slice<Feed> {
        return feedEntityV2CassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerId(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            pageable = pageable,
        ).map { entity -> entity.toFeed() }
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeySortKeyLessThan(
        workspaceId: String,
        componentId: String,
        ownerId: String,
        sortKey: Long,
        pageable: Pageable,
    ): Slice<Feed> {
        return feedEntityV2CassandraRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyOwnerIdAndKeySortKeyLessThan(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            sortKey = sortKey,
            pageable = pageable,
        ).map { entity -> entity.toFeed() }
    }

}
