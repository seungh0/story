package com.story.core.domain.apikey

import com.story.core.common.model.AuditingTime
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("api_key_v1")
data class ApiKey(
    @field:PrimaryKey
    val apiKey: String,
    val workspaceId: String,
    var status: ApiKeyStatus,
    val description: String,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    var auditingTime: AuditingTime,
) {

    companion object {
        fun of(
            workspaceId: String,
            apiKey: String,
            status: ApiKeyStatus = ApiKeyStatus.ENABLED,
            description: String,
        ) = ApiKey(
            apiKey = apiKey,
            workspaceId = workspaceId,
            description = description,
            status = status,
            auditingTime = AuditingTime.created(),
        )

        fun from(workspaceApiKey: WorkspaceApiKey) = ApiKey(
            apiKey = workspaceApiKey.key.apiKey,
            workspaceId = workspaceApiKey.key.workspaceId,
            status = workspaceApiKey.status,
            description = workspaceApiKey.description,
            auditingTime = AuditingTime.created(),
        )
    }

}
