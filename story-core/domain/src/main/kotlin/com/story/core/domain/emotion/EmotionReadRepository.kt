package com.story.core.domain.emotion

import com.story.core.domain.resource.ResourceId
import org.springframework.data.domain.Pageable

interface EmotionReadRepository {

    suspend fun existsById(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
    ): Boolean

    suspend fun findAllById(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionIds: Set<String>,
    ): List<Emotion>

    suspend fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentId(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        pageable: Pageable,
    ): List<Emotion>

    suspend fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdAndKeyEmotionIdIn(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionIds: Set<String>,
    ): List<Emotion>

}
