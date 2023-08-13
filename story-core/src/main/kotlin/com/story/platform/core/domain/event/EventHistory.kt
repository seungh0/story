package com.story.platform.core.domain.event

import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

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
            resourceId: ResourceId,
            componentId: String,
            eventRecord: EventRecord<T>,
        ): EventHistory {
            val slotId = EventIdHelper.getSlot(snowflake = eventRecord.eventId)
            return EventHistory(
                key = EventHistoryPrimaryKey(
                    workspaceId = workspaceId,
                    slotId = slotId,
                    eventId = eventRecord.eventId,
                    resourceId = resourceId,
                    componentId = componentId,
                    eventAction = eventRecord.eventAction,
                ),
                publishStatus = EventPublishStatus.SUCCESS,
                payloadJson = eventRecord.payload.toJson(),
            )
        }

        fun <T> failed(
            workspaceId: String,
            resourceId: ResourceId,
            componentId: String,
            eventRecord: EventRecord<T>,
            exception: Throwable,
        ): EventHistory {
            return EventHistory(
                key = EventHistoryPrimaryKey.of(
                    workspaceId = workspaceId,
                    eventId = eventRecord.eventId,
                    resourceId = resourceId,
                    componentId = componentId,
                    eventAction = eventRecord.eventAction,
                ),
                publishStatus = EventPublishStatus.SUCCESS,
                payloadJson = eventRecord.payload.toJson(),
                failureReason = exception.message + "[${exception.cause?.message}]"
            )
        }
    }

}

@PrimaryKeyClass
data class EventHistoryPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val slotId: Long,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 3)
    val eventId: Long,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 4)
    val resourceId: ResourceId,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    val componentId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 6)
    val eventAction: EventAction,
) {

    companion object {
        fun of(
            workspaceId: String,
            eventId: Long,
            resourceId: ResourceId,
            componentId: String,
            eventAction: EventAction,
        ) = EventHistoryPrimaryKey(
            workspaceId = workspaceId,
            slotId = EventIdHelper.getSlot(snowflake = eventId),
            eventId = eventId,
            resourceId = resourceId,
            componentId = componentId,
            eventAction = eventAction,
        )
    }

}
