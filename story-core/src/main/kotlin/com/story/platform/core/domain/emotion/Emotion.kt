package com.story.platform.core.domain.emotion

import com.story.platform.core.common.model.AuditingTime
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("emotion_v1")
data class Emotion(
    @field:PrimaryKey
    val key: EmotionPrimaryKey,
    var image: String,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    val auditingTime: AuditingTime,
) {

    fun patch(
        image: String?,
    ): Boolean {
        var hasChanged = false
        if (image != null) {
            hasChanged = hasChanged || this.image != image
            this.image = image
        }

        this.auditingTime.updated()

        return hasChanged
    }

    companion object {
        fun of(
            workspaceId: String,
            resourceId: ResourceId,
            componentId: String,
            emotionId: String,
            image: String,
        ) = Emotion(
            key = EmotionPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                emotionId = emotionId,
            ),
            image = image,
            auditingTime = AuditingTime.created(),
        )
    }

}

@PrimaryKeyClass
data class EmotionPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val resourceId: ResourceId,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING, ordinal = 4)
    val emotionId: String,
)
