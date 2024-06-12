package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import com.story.core.support.cassandra.executeCoroutine
import com.story.core.support.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class FeedMappingEntityRepository(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val feedMappingCassandraRepository: FeedMappingCassandraRepository,
    private val feedMappingReverseCassandraRepository: FeedMappingReverseCassandraRepository,
) : FeedMappingWriteRepository, FeedMappingReadRepository {

    override suspend fun existsById(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
    ): Boolean {
        return feedMappingCassandraRepository.existsById(
            FeedMappingPrimaryKey(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                sourceResourceId = sourceResourceId,
                sourceComponentId = sourceComponentId,
                subscriptionComponentId = subscriptionComponentId,
            )
        )
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySourceResourceIdAndKeySourceComponentId(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        pageable: Pageable,
    ): Slice<FeedMapping> {
        val feedMappings = feedMappingCassandraRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySourceResourceIdAndKeySourceComponentId(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
            pageable = pageable,
        )
        return SliceImpl(
            feedMappings.content.map { entity -> entity.toFeedMapping() },
            feedMappings.nextPageable(),
            feedMappings.hasNext()
        )
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeySourceResourceIdAndKeySourceComponentId(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        pageable: Pageable,
    ): Slice<FeedMapping> {
        val feedMappings = feedMappingReverseCassandraRepository.findAllByKeyWorkspaceIdAndKeySourceResourceIdAndKeySourceComponentId(
            workspaceId = workspaceId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
            pageable = CassandraPageRequest.first(3),
        )
        return SliceImpl(
            feedMappings.content.map { entity -> entity.toFeedMapping() },
            feedMappings.nextPageable(),
            feedMappings.hasNext()
        )
    }

    override suspend fun create(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
        description: String,
        retention: Duration,
    ) {
        val feedMapping = FeedMappingEntity.of(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
            subscriptionComponentId = subscriptionComponentId,
            description = description,
            retention = retention,
        )
        reactiveCassandraOperations.batchOps()
            .upsert(feedMapping)
            .upsert(FeedMappingReverseEntity.of(feedMapping))
            .executeCoroutine()
    }

    override suspend fun delete(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
    ) {
        val feedMappingKey = FeedMappingPrimaryKey(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
            subscriptionComponentId = subscriptionComponentId,
        )

        val feedMapping = feedMappingCassandraRepository.findById(feedMappingKey)
            ?: throw FeedMappingNotExistsException("피드 연동 설정($feedMappingKey)이 존재하지 않습니다")

        reactiveCassandraOperations.batchOps()
            .delete(feedMapping)
            .delete(FeedMappingReverseEntity.of(feedMapping))
            .executeCoroutine()
    }

}
