package com.story.core.domain.emotion

import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorResponse
import com.story.core.common.utils.mapToSet
import com.story.core.domain.emotion.EmotionPolicy.EMOTION_MAX_COUNT_PER_COMPONENT
import com.story.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class EmotionReader(
    private val emotionReadRepository: EmotionReadRepository,
) {

    suspend fun validateExistsEmotions(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionIds: Set<String>,
    ) {
        val emotions = emotionReadRepository.findAllById(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            emotionIds = emotionIds,
        ).toList()

        if (emotions.size < emotionIds.size) {
            val existsEmotionIds = emotions.mapToSet { emotion -> emotion.emotionId }
            throw EmotionNotExistsException("워크스페이스($workspaceId)의 리소스/컴포넌트($resourceId/$componentId)에 존재하지 않는 이모션(${emotionIds - existsEmotionIds})이 존재합니다")
        }
    }

    suspend fun getEmotions(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionIds: Set<String>,
    ): Map<String, Emotion> {
        val emotions = emotionReadRepository.findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdAndKeyEmotionIdIn(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            emotionIds = emotionIds,
        ).toList()
        return emotions.associateBy { emotion -> emotion.emotionId }
    }

    suspend fun listEmotions(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ): Slice<Emotion, String> {
        val emotions = emotionReadRepository.findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentId(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            pageable = CassandraPageRequest.first(EMOTION_MAX_COUNT_PER_COMPONENT),
        ).toList()

        return Slice.of(
            data = emotions,
            cursor = CursorResponse.noMore(),
        )
    }

}
