package com.story.platform.core.domain.event

import com.story.platform.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDateTime

/**
 * TTL: 30일
 * - compaction: Time Window Compaction Strategy
 * - gc seconds: 적정 값 찾기..
 */
@Table("event_history_reverse_v1")
data class EventHistoryReverse(
    @field:PrimaryKey
    val key: EventHistoryReversePrimaryKey,
    val publishedStatus: EventPublishedStatus,
    val payloadJson: String,
) {

    companion object {
        fun of(
            eventHistory: EventHistory,
        ) = EventHistoryReverse(
            key = EventHistoryReversePrimaryKey(
                workspaceId = eventHistory.key.workspaceId,
                componentId = eventHistory.key.componentId,
                resourceId = eventHistory.key.resourceId,
                eventAction = eventHistory.key.eventAction,
                eventId = eventHistory.key.eventId,
                eventDate = eventHistory.key.eventDate,
                timestamp = eventHistory.key.timestamp,
            ),
            publishedStatus = eventHistory.publishedStatus,
            payloadJson = eventHistory.payloadJson,
        )
    }

}

@PrimaryKeyClass
data class EventHistoryReversePrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val resourceId: ResourceId,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 4)
    val eventAction: EventAction,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 5)
    val eventId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 6)
    val eventDate: String, // yyyyMMddTHH:mm -> 1분 단위?

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 7)
    val timestamp: LocalDateTime,
)
