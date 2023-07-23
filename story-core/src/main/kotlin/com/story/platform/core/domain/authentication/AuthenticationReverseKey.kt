package com.story.platform.core.domain.authentication

import com.story.platform.core.common.model.AuditingTime
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("authentication_reverse_key_v1")
data class AuthenticationReverseKey(
    @field:PrimaryKey
    val key: AuthenticationReverseKeyPrimaryKey,

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
        ) = AuthenticationKey(
            key = AuthenticationKeyPrimaryKey(
                workspaceId = workspaceId,
                authenticationKey = apiKey,
            ),
            description = description,
            status = status,
            auditingTime = AuditingTime.created(),
        )

        fun from(authenticationKey: AuthenticationKey) = AuthenticationReverseKey(
            key = AuthenticationReverseKeyPrimaryKey(
                authenticationKey = authenticationKey.key.authenticationKey,
            ),
            workspaceId = authenticationKey.key.workspaceId,
            status = authenticationKey.status,
            description = authenticationKey.description,
        )
    }

}

@PrimaryKeyClass
data class AuthenticationReverseKeyPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val authenticationKey: String,
)
