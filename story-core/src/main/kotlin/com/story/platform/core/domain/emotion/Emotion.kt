package com.story.platform.core.domain.emotion

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("emotion_v1")
data class Emotion(
    @field:PrimaryKey
    val key: EmotionPrimaryKey,
    val image: String,
) {

    companion object {
        fun of(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            emotionId: String,
            image: String,
        ) = Emotion(
            key = EmotionPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                emotionId = emotionId,
            ),
            image = image,
        )
    }

}

@PrimaryKeyClass
data class EmotionPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val spaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING, ordinal = 4)
    val emotionId: String,
)
