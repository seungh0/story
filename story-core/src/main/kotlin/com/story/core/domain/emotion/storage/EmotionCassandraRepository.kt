package com.story.core.domain.emotion.storage

import com.story.core.domain.resource.ResourceId
import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable

interface EmotionCassandraRepository : CassandraBasicRepository<EmotionEntity, EmotionPrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentId(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        pageable: Pageable,
    ): Flow<EmotionEntity>

    fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdAndKeyEmotionIdIn(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionIds: Collection<String>,
    ): Flow<EmotionEntity>

}
