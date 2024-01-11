package com.story.core.domain.event

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

/**
 * event_key에 해당하는 event_id를 찾는 테이블
 */
@Table("event_key_id_mapping_v1")
data class EventKeyIdMapping(
    @field:PrimaryKey
    val key: EventKeyIdMappingPrimaryKey,
) {

    companion object {
        fun of(
            workspaceId: String,
            eventKey: String,
            eventId: Long,
        ) = EventKeyIdMapping(
            key = EventKeyIdMappingPrimaryKey(
                workspaceId = workspaceId,
                eventKey = eventKey,
                eventId = eventId,
            )
        )
    }

}

@PrimaryKeyClass
data class EventKeyIdMappingPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val eventKey: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 3)
    val eventId: Long,
)
