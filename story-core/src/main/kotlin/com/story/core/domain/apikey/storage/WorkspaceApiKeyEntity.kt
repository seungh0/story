package com.story.core.domain.apikey.storage

import com.story.core.common.model.AuditingTimeEntity
import com.story.core.domain.apikey.ApiKeyStatus
import com.story.core.infrastructure.cassandra.CassandraEntity
import com.story.core.infrastructure.cassandra.CassandraKey
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("workspace_api_key_v1")
data class WorkspaceApiKey(
    @field:PrimaryKey
    override val key: WorkspaceApiKeyPrimaryKey,

    var status: ApiKeyStatus,
    var description: String = "",

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    var auditingTime: AuditingTimeEntity,
) : CassandraEntity {

    fun patch(
        description: String?,
        status: ApiKeyStatus?,
    ) {
        if (description != null) {
            this.description = description
        }

        if (status != null) {
            this.status = status
        }

        this.auditingTime = this.auditingTime.updated()
    }

    companion object {
        fun of(
            workspaceId: String,
            apiKey: String,
            status: ApiKeyStatus = ApiKeyStatus.ENABLED,
            description: String,
        ) = WorkspaceApiKey(
            key = WorkspaceApiKeyPrimaryKey(
                workspaceId = workspaceId,
                apiKey = apiKey,
            ),
            description = description,
            status = status,
            auditingTime = AuditingTimeEntity.created(),
        )
    }

}

@PrimaryKeyClass
data class WorkspaceApiKeyPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 2)
    val apiKey: String,
) : CassandraKey
