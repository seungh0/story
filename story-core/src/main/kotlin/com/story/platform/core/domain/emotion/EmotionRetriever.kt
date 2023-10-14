package com.story.platform.core.domain.emotion

import com.story.platform.core.common.model.ContentsWithCursor
import com.story.platform.core.common.model.CursorUtils
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.common.utils.mapToSet
import com.story.platform.core.domain.resource.ResourceId
import kotlinx.coroutines.flow.toList
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class EmotionRetriever(
    private val emotionRepository: EmotionRepository,
) {

    suspend fun validateExistsEmotions(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        emotionIds: Set<String>,
    ) {
        val keys = emotionIds.map { emotionId ->
            EmotionPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                emotionId = emotionId,
            )
        }
        val emotions = emotionRepository.findAllById(keys).toList()

        if (emotions.size < emotionIds.size) {
            val existsEmotionIds = emotions.mapToSet { emotion -> emotion.key.emotionId }
            throw EmotionNotExistsException("존재하지 않는 이모션(${emotionIds - existsEmotionIds})이 존재합니다")
        }
    }

    suspend fun listEmotions(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        cursorRequest: CursorRequest,
    ): ContentsWithCursor<EmotionResponse, String> {
        val emotions = if (cursorRequest.cursor.isNullOrBlank()) {
            emotionRepository.findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentId(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            ).toList()
        } else {
            emotionRepository.findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdAndKeyEmotionIdGreaterThan(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                emotionId = cursorRequest.cursor,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            ).toList()
        }

        return ContentsWithCursor.of(
            data = emotions.map { emotion -> EmotionResponse.of(emotion = emotion) },
            cursor = CursorUtils.getCursor(
                listWithNextCursor = emotions.toList(),
                pageSize = cursorRequest.pageSize,
                keyGenerator = { emotion -> emotion?.key?.emotionId }
            )
        )
    }

}
