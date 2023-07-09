package com.story.platform.core.domain.feed

import org.springframework.stereotype.Service

@Service
class FeedMappingModifier(
    private val feedMappingConfigurationRepository: FeedMappingConfigurationRepository,
) {

    suspend fun modify(
        request: FeedMappingModifyRequest,
    ) {
        val configuration = feedMappingConfigurationRepository.findById(request.toConfigurationKey())
            ?: throw FeedMappingNotExistsException("워크스페이스(${request.workspaceId})의 리소스(${request.resourceId})의 컴포넌트(${request.componentId})의 이벤트(${request.eventAction})에 대해서 구독(${request.subscriptionComponentId})와 피드 연동 설정이 없습니다")

        configuration.patch(
            description = request.description,
            status = request.status,
        )
        feedMappingConfigurationRepository.save(configuration)
    }

}
