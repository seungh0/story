package com.story.platform.core.domain.authentication

import com.story.platform.core.common.enums.ServiceType
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

    val serviceType: ServiceType,
    var status: AuthenticationKeyStatus,
) {

    fun patch(
        status: AuthenticationKeyStatus?,
    ): Boolean {
        var hasChanged = false
        if (status != null) {
            hasChanged = hasChanged || this.status != status
            this.status = status
        }
        return hasChanged
    }

    companion object {
        fun of(
            serviceType: ServiceType,
            apiKey: String,
            status: AuthenticationKeyStatus = AuthenticationKeyStatus.ENABLED,
            description: String,
        ) = AuthenticationKey(
            key = AuthenticationKeyPrimaryKey(
                serviceType = serviceType,
                apiKey = apiKey,
            ),
            description = description,
            status = status,
            auditingTime = AuditingTime.newEntity(),
        )

        fun from(authenticationKey: AuthenticationKey) = AuthenticationReverseKey(
            key = AuthenticationReverseKeyPrimaryKey(
                apiKey = authenticationKey.key.apiKey,
            ),
            serviceType = authenticationKey.key.serviceType,
            status = authenticationKey.status,
        )
    }

}

@PrimaryKeyClass
data class AuthenticationReverseKeyPrimaryKey(
    @field:PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 1)
    val apiKey: String,
)
