package com.story.core.domain.emotion

import com.story.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class EmotionModifier(
    private val emotionRepository: EmotionRepository,
) {

    suspend fun modifyEmotion(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
        priority: Long?,
        image: String?,
    ) {
        val key = EmotionPrimaryKey(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            emotionId = emotionId,
        )
        val emotion = emotionRepository.findById(key)
            ?: throw EmotionNotExistsException("워크스페이스($workspaceId)의 리소스/컴포넌트($resourceId/$componentId)에 존재하지 않는 이모션($emotionId)입니다")
        emotion.patch(priority = priority, image = image)
        emotionRepository.save(emotion)
    }

}
