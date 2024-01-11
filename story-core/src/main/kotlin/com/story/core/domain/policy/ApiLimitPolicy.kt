package com.story.core.domain.policy

import com.story.core.common.model.AuditingTime
import com.story.core.domain.event.EventAction
import com.story.core.domain.resource.ResourceId
import com.story.core.domain.workspace.WorkspacePricePlan
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Duration

/**
 * - Duration
 * "PT20.345S" -- parses as "20.345 seconds"
 * "PT15M"     -- parses as "15 minutes" (where a minute is 60 seconds)
 * "PT10H"     -- parses as "10 hours" (where an hour is 3600 seconds)
 * "P2D"       -- parses as "2 days" (where a day is 24 hours or 86400 seconds)
 * "P2DT3H4M"  -- parses as "2 days, 3 hours and 4 minutes"
 */
@Table("api_limit_policy_v1")
data class ApiLimitPolicy(
    @field:PrimaryKey
    val key: ApiLimitPolicyPrimaryKey,

    val duration: Duration,
    val limitCount: Long,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    val auditingTime: AuditingTime,
)

@PrimaryKeyClass
data class ApiLimitPolicyPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val plan: WorkspacePricePlan,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 2)
    val resourceId: ResourceId,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 3)
    val eventAction: EventAction,
)
