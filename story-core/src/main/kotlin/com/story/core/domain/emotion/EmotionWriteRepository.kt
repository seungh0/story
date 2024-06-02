package com.story.core.domain.emotion

import com.story.core.domain.resource.ResourceId

interface EmotionWriteRepository {

    suspend fun create(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
        priority: Long,
        image: String,
    ): Emotion

    suspend fun update(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
        priority: Long?,
        image: String?,
    ): Emotion

}
