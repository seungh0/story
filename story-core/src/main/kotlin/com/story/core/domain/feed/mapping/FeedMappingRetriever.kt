package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cache.Cacheable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class FeedMappingRetriever(
    private val feedMappingReverseRepository: FeedMappingReverseRepository,
) {

    @Cacheable(
        cacheType = CacheType.FEED_MAPPING,
        key = "'workspaceId:' + {#workspaceId} + ':sourceResourceId:' + {#sourceResourceId} + ':sourceComponentId:' + {#sourceComponentId}",
    )
    suspend fun listConnectedFeedMappings(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
    ): List<FeedMappingResponse> {
        val feedMappings = feedMappingReverseRepository.findAllByKeyWorkspaceIdAndKeySourceResourceIdAndKeySourceComponentId(
            workspaceId = workspaceId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
            pageable = CassandraPageRequest.first(3),
        )
        return feedMappings.map { feedMapping -> FeedMappingResponse.of(feedMapping) }.toList()
    }

}
