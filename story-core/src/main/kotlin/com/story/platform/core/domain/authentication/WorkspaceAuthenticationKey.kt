package com.story.platform.core.domain.authentication

import com.story.platform.core.common.model.AuditingTime
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("workspace_authentication_key_v1")
data class WorkspaceAuthenticationKey(
    @field:PrimaryKey
    val key: WorkspaceAuthenticationPrimaryKey,

    var status: AuthenticationKeyStatus,
    var description: String = "",

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    var auditingTime: AuditingTime,
) {

    fun patch(
        description: String?,
        status: AuthenticationKeyStatus?,
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
            authenticationKey: String,
            status: AuthenticationKeyStatus = AuthenticationKeyStatus.ENABLED,
            description: String,
        ) = WorkspaceAuthenticationKey(
            key = WorkspaceAuthenticationPrimaryKey(
                workspaceId = workspaceId,
                authenticationKey = authenticationKey,
            ),
            description = description,
            status = status,
            auditingTime = AuditingTime.created(),
        )
    }

}

@PrimaryKeyClass
data class WorkspaceAuthenticationPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 2)
    val authenticationKey: String,
)
