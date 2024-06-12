package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import com.story.core.support.cache.CacheType
import com.story.core.support.cache.Cacheable
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class FeedMappingReader(
    private val feedMappingReadRepository: FeedMappingReadRepository,
) {

    @Cacheable(
        cacheType = CacheType.FEED_MAPPING,
        key = "'workspaceId:' + {#workspaceId} + ':sourceResourceId:' + {#sourceResourceId} + ':sourceComponentId:' + {#sourceComponentId}",
    )
    suspend fun listConnectedFeedMappings(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
    ): List<FeedMapping> {
        return feedMappingReadRepository.findAllByKeyWorkspaceIdAndKeySourceResourceIdAndKeySourceComponentId(
            workspaceId = workspaceId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
            pageable = CassandraPageRequest.first(3),
        ).content
    }

}
