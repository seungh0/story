package com.story.platform.core.domain.event

import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.support.json.toJson
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * TTL: 30일
 * - compaction: Time Window Compaction Strategy
 * - gc seconds: 적정 값 찾기..
 */
@Table("event_history_v1")
data class EventHistory(
    @field:PrimaryKey
    val key: EventHistoryPrimaryKey,

    val publishStatus: EventPublishStatus,
    val failureReason: String = "",
    val payloadJson: String,
) {

    companion object {
        fun <T> success(
            workspaceId: String,
            componentId: String,
            eventRecord: EventRecord<T>,
        ) =
            EventHistory(
                key = EventHistoryPrimaryKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    resourceId = eventRecord.resourceId,
                    eventAction = eventRecord.eventAction,
                    eventDate = eventRecord.timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'hh:mm")),
                    timestamp = eventRecord.timestamp,
                    eventId = eventRecord.eventId,
                ),
                publishStatus = EventPublishStatus.SUCCESS,
                payloadJson = eventRecord.payload.toJson(),
            )

        fun <T> failed(
            workspaceId: String,
            componentId: String,
            eventRecord: EventRecord<T>,
            exception: Exception,
        ) =
            EventHistory(
                key = EventHistoryPrimaryKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    resourceId = eventRecord.resourceId,
                    eventAction = eventRecord.eventAction,
                    eventDate = eventRecord.timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'hh:mm")),
                    timestamp = eventRecord.timestamp,
                    eventId = eventRecord.eventId,
                ),
                publishStatus = EventPublishStatus.FAILED,
                payloadJson = eventRecord.payload.toJson(),
                failureReason = exception.message ?: "${exception.javaClass.name} 에러가 발생하였습니다",
            )
    }

}

@PrimaryKeyClass
data class EventHistoryPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val resourceId: ResourceId,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 3)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 4)
    val eventAction: EventAction,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 5)
    val eventDate: String, // yyyyMMddTHH:mm -> 1분 단위?

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 6)
    val timestamp: LocalDateTime,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 7)
    val eventId: String,
)
