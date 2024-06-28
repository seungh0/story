package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import com.story.core.support.cache.CacheType
import com.story.core.support.cache.Cacheable
import org.springframework.stereotype.Service

@Service
class FeedMappingReaderWithCache(
    private val feedMappingReader: FeedMappingReader,
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
        return feedMappingReader.listConnectedFeedMappings(
            workspaceId = workspaceId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
        )
    }

}
