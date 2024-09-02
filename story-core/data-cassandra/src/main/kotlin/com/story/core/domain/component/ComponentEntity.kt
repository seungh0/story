package com.story.core.domain.component

import com.story.core.common.model.AuditingTimeEntity
import com.story.core.domain.resource.ResourceId
import com.story.core.support.cassandra.CassandraEntity
import com.story.core.support.cassandra.CassandraKey
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("component_v1")
data class ComponentEntity(
    @field:PrimaryKey
    override val key: ComponentPrimaryKey,

    var status: ComponentStatus,

    var description: String,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    var auditingTime: AuditingTimeEntity,
) : CassandraEntity {

    fun toComponent() = Component(
        componentId = this.key.componentId,
        status = this.status,
        description = this.description,
    ).apply { this.setAuditingTime(auditingTime) }

    fun patch(description: String?, status: ComponentStatus?) {
        if (description != null) {
            this.description = description
        }

        if (status != null) {
            this.status = status
        }

        this.auditingTime = auditingTime.updated()
    }

    companion object {
        fun of(
            workspaceId: String,
            resourceId: ResourceId,
            componentId: String,
            description: String,
            status: ComponentStatus,
        ) = ComponentEntity(
            key = ComponentPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            ),
            status = status,
            description = description,
            auditingTime = AuditingTimeEntity.created(),
        )
    }

}

@PrimaryKeyClass
data class ComponentPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 2)
    val resourceId: ResourceId,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 3)
    val componentId: String,
) : CassandraKey
