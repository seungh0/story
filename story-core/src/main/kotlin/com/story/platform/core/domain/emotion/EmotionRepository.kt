package com.story.platform.core.domain.emotion

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface EmotionRepository : CoroutineCrudRepository<Emotion, EmotionPrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        pageable: Pageable,
    ): Flow<Emotion>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyEmotionIdGreaterThan(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        emotionId: String,
        pageable: Pageable,
    ): Flow<Emotion>

}
