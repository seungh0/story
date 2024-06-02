package com.story.core.domain.emotion

import com.story.core.domain.emotion.storage.EmotionCassandraRepository
import com.story.core.domain.emotion.storage.EmotionEntity
import com.story.core.domain.emotion.storage.EmotionPrimaryKey
import com.story.core.domain.resource.ResourceId
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class EmotionEntityRepository(
    private val emotionCassandraRepository: EmotionCassandraRepository,
) : EmotionWriteRepository, EmotionReadRepository {

    override suspend fun create(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
        priority: Long,
        image: String,
    ): Emotion {
        val entity = EmotionEntity.of(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            emotionId = emotionId,
            priority = priority,
            image = image,
        )
        emotionCassandraRepository.save(entity)
        return Emotion.from(entity)
    }

    override suspend fun update(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
        priority: Long?,
        image: String?,
    ): Emotion {
        val key = EmotionPrimaryKey(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            emotionId = emotionId,
        )
        val emotion = emotionCassandraRepository.findById(key)
            ?: throw EmotionNotExistsException("워크스페이스($workspaceId)의 리소스/컴포넌트($resourceId/$componentId)에 존재하지 않는 이모션($emotionId)입니다")
        emotion.patch(priority = priority, image = image)
        emotionCassandraRepository.save(emotion)
        return Emotion.from(emotion)
    }

    override suspend fun existsById(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionId: String,
    ): Boolean {
        return emotionCassandraRepository.existsById(
            EmotionPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                emotionId = emotionId,
            )
        )
    }

    override suspend fun findAllById(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionIds: Set<String>,
    ): List<Emotion> {
        val keys = emotionIds.map { emotionId ->
            EmotionPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                emotionId = emotionId,
            )
        }
        return emotionCassandraRepository.findAllById(keys).toList()
            .map { entity -> Emotion.from(entity) }
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentId(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        pageable: Pageable,
    ): List<Emotion> {
        val entities = emotionCassandraRepository.findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentId(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            pageable = pageable,
        ).toList()
        return entities.map { entity -> Emotion.from(entity) }
    }

    override suspend fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdAndKeyEmotionIdIn(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionIds: Set<String>,
    ): List<Emotion> {
        val entities = emotionCassandraRepository.findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdAndKeyEmotionIdIn(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            emotionIds = emotionIds,
        ).toList()
        return entities.map { entity -> Emotion.from(entity) }
    }

}
