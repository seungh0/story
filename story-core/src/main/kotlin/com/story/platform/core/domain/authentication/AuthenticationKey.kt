package com.story.platform.core.domain.authentication

import com.story.platform.core.common.model.AuditingTime
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("authentication_key_v1")
data class AuthenticationKey(
    @field:PrimaryKey
    val key: AuthenticationKeyPrimaryKey,

    val workspaceId: String,
    var status: AuthenticationKeyStatus,
    val description: String,
) {

    companion object {
        fun of(
            workspaceId: String,
            apiKey: String,
            status: AuthenticationKeyStatus = AuthenticationKeyStatus.ENABLED,
            description: String,
        ) = WorkspaceAuthenticationKey(
            key = WorkspaceAuthenticationPrimaryKey(
                workspaceId = workspaceId,
                authenticationKey = apiKey,
            ),
            description = description,
            status = status,
            auditingTime = AuditingTime.created(),
        )

        fun from(workspaceAuthenticationKey: WorkspaceAuthenticationKey) = AuthenticationKey(
            key = AuthenticationKeyPrimaryKey(
                authenticationKey = workspaceAuthenticationKey.key.authenticationKey,
            ),
            workspaceId = workspaceAuthenticationKey.key.workspaceId,
            status = workspaceAuthenticationKey.status,
            description = workspaceAuthenticationKey.description,
        )
    }

}

@PrimaryKeyClass
data class AuthenticationKeyPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val authenticationKey: String,
)
