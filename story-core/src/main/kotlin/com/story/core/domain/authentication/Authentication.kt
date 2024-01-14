package com.story.core.domain.authentication

import com.story.core.common.model.AuditingTime
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("authentication_v1")
data class Authentication(
    @field:PrimaryKey
    val authenticationKey: String,
    val workspaceId: String,
    var status: AuthenticationStatus,
    val description: String,
) {

    companion object {
        fun of(
            workspaceId: String,
            authenticationKey: String,
            status: AuthenticationStatus = AuthenticationStatus.ENABLED,
            description: String,
        ) = WorkspaceAuthentication(
            key = WorkspaceAuthenticationPrimaryKey(
                workspaceId = workspaceId,
                authenticationKey = authenticationKey,
            ),
            description = description,
            status = status,
            auditingTime = AuditingTime.created(),
        )

        fun from(workspaceAuthentication: WorkspaceAuthentication) = Authentication(
            authenticationKey = workspaceAuthentication.key.authenticationKey,
            workspaceId = workspaceAuthentication.key.workspaceId,
            status = workspaceAuthentication.status,
            description = workspaceAuthentication.description,
        )
    }

}
