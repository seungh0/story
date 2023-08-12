package com.story.platform.core.domain.feed.mapping

import com.story.platform.core.domain.resource.ResourceId
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class FeedMappingRetriever(
    private val feedReverseMappingConfigurationRepository: FeedReverseMappingConfigurationRepository,
) {

    suspend fun listConnectedFeedMappings(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
    ): List<FeedMappingResponse> {
        val feedMappings = feedReverseMappingConfigurationRepository.findAllByKeyWorkspaceIdAndKeySourceResourceIdAndKeySourceComponentId(
            workspaceId = workspaceId,
            sourceResourceId = sourceResourceId,
            sourceComponentId = sourceComponentId,
            pageable = CassandraPageRequest.first(3),
        )
        return feedMappings.map { feedMapping -> FeedMappingResponse.of(feedMapping) }.toList()
    }

}