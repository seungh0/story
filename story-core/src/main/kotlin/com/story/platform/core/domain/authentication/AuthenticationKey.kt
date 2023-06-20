package com.story.platform.core.domain.authentication

import com.story.platform.core.common.model.AuditingTime
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("authentication_key_v1")
data class AuthenticationKey(
    @field:PrimaryKey
    val key: AuthenticationKeyPrimaryKey,

    var status: AuthenticationKeyStatus,
    var description: String = "",

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    var auditingTime: AuditingTime,
) {

    fun patch(
        description: String?,
        status: AuthenticationKeyStatus?,
    ): Boolean {
        var hasChanged = false
        if (description != null) {
            hasChanged = hasChanged || this.description != description
            this.description = description
        }

        if (status != null) {
            hasChanged = hasChanged || this.status != status
            this.status = status
        }

        if (hasChanged) {
            this.auditingTime = this.auditingTime.updated()
        }

        return hasChanged
    }

    companion object {
        fun of(
            workspaceId: String,
            apiKey: String,
            status: AuthenticationKeyStatus = AuthenticationKeyStatus.ENABLED,
            description: String,
        ) = AuthenticationKey(
            key = AuthenticationKeyPrimaryKey(
                workspaceId = workspaceId,
                apiKey = apiKey,
            ),
            description = description,
            status = status,
            auditingTime = AuditingTime.newEntity(),
        )
    }

}

@PrimaryKeyClass
data class AuthenticationKeyPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val workspaceId: String,

    @field:PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 2)
    val apiKey: String,
)
