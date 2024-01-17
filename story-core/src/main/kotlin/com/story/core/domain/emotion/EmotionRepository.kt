package com.story.core.domain.emotion

import com.story.core.domain.resource.ResourceId
import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable

interface EmotionRepository : CassandraBasicRepository<Emotion, EmotionPrimaryKey> {

    fun findAllByKeyIn(keys: Collection<EmotionPrimaryKey>): Flow<Emotion>

    suspend fun deleteByKey(key: EmotionPrimaryKey)

    fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentId(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        pageable: Pageable,
    ): Flow<Emotion>

    fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdAndKeyEmotionIdIn(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionIds: Collection<String>,
    ): Flow<Emotion>

}
