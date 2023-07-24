package com.story.platform.core.domain.feed.configuration

import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class FeedMappingConnector(
    private val feedMappingConfigurationRepository: FeedMappingConfigurationRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun connect(
        request: FeedMappingConnectRequest,
    ) {
        if (feedMappingConfigurationRepository.existsById(request.toConfigurationKey())) {
            throw FeedMappingAlreadyConnectedException("이미 워크스페이스(${request.workspaceId})의 리소스(${request.resourceId})의 컴포넌트(${request.componentId})의 이벤트(${request.eventAction})에 대해서 구독(${request.targetComponentId})와 피드 연동 설정이 등록되어 있습니다")
        }
        val feedMappingConfiguration = request.toConfiguration()
        reactiveCassandraOperations.batchOps()
            .upsert(feedMappingConfiguration)
            .insert(FeedReverseMappingConfiguration.of(feedMappingConfiguration))
            .executeCoroutine()
    }

}
