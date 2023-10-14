package com.story.platform.core.domain.emotion

import com.story.platform.core.common.model.ContentsWithCursor
import com.story.platform.core.common.model.CursorUtils
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.common.utils.mapToSet
import kotlinx.coroutines.flow.toList
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class EmotionRetriever(
    private val emotionRepository: EmotionRepository,
) {

    suspend fun validateExistsEmotions(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        emotionIds: Set<String>,
    ) {
        val keys = emotionIds.map { emotionId ->
            EmotionPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
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
        componentId: String,
        spaceId: String,
        cursor: CursorRequest,
    ): ContentsWithCursor<EmotionResponse, String> {
        val emotions = if (cursor.cursor.isNullOrBlank()) {
            emotionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceId(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                pageable = CassandraPageRequest.first(cursor.pageSize + 1),
            ).toList()
        } else {
            emotionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyEmotionIdGreaterThan(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                emotionId = cursor.cursor,
                pageable = CassandraPageRequest.first(cursor.pageSize + 1),
            ).toList()
        }

        return ContentsWithCursor.of(
            data = emotions.map { emotion -> EmotionResponse.of(emotion = emotion) },
            cursor = CursorUtils.getCursor(
                listWithNextCursor = emotions.toList(),
                pageSize = cursor.pageSize,
                keyGenerator = { emotion -> emotion?.key?.emotionId }
            )
        )
    }

}
