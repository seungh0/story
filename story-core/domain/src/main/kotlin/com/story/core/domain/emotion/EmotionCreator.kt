package com.story.core.domain.emotion

import com.story.core.domain.emotion.EmotionPolicy.EMOTION_MAX_COUNT_PER_COMPONENT
import com.story.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class EmotionCreator(
    private val emotionWriteRepository: EmotionWriteRepository,
    private val emotionReadRepository: EmotionReadRepository,
) {

    suspend fun createEmotion(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
        priority: Long,
        image: String,
    ) {
        validateNotExistsEmotion(workspaceId, resourceId, componentId, emotionId)
        validateNotExceededEmotionCountLimit(workspaceId, resourceId, componentId)

        emotionWriteRepository.create(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            emotionId = emotionId,
            priority = priority,
            image = image,
        )
    }

    private suspend fun validateNotExistsEmotion(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
    ) {
        if (emotionReadRepository.existsById(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                emotionId = emotionId,
            )
        ) {
            throw EmotionAlreadyExistsException("워크스페이스($workspaceId)의 리소스/컴포넌트($resourceId/$componentId)에 이미 등록된 이모션($emotionId) 입니다")
        }
    }

    private suspend fun validateNotExceededEmotionCountLimit(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ) {
        val existsEmotions = emotionReadRepository.findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentId(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            pageable = CassandraPageRequest.first(EMOTION_MAX_COUNT_PER_COMPONENT),
        )

        if (existsEmotions.toList().size >= EMOTION_MAX_COUNT_PER_COMPONENT) {
            throw EmotionCountLimitExceedException("워크스페이스($workspaceId)의 리소스/컴포넌트($resourceId/$componentId)에 등록할 수 있는 최대 이모션 갯수($EMOTION_MAX_COUNT_PER_COMPONENT)을 넘어서, 더이상 등록할 수 없습니다")
        }
    }

}
