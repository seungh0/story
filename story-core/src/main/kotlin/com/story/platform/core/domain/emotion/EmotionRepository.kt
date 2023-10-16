package com.story.platform.core.domain.emotion

import com.story.platform.core.domain.resource.ResourceId
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface EmotionRepository : CoroutineCrudRepository<Emotion, EmotionPrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentId(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        pageable: Pageable,
    ): Flow<Emotion>

    fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentId(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ): Flow<Emotion>

    fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdAndKeyEmotionIdIn(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionIds: Collection<String>,
    ): Flow<Emotion>

}
