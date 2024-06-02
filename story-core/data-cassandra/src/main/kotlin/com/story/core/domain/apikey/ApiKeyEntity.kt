package com.story.core.domain.apikey

import com.story.core.common.model.AuditingTimeEntity
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("api_key_v1")
data class ApiKeyEntity(
    @field:PrimaryKey
    val apiKey: String,
    val workspaceId: String,
    var status: ApiKeyStatus,
    val description: String,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    var auditingTime: AuditingTimeEntity,
) {

    fun toApiKey() = ApiKey(
        workspaceId = workspaceId,
        apiKey = apiKey,
        status = status,
        description = description,
    )

    companion object {
        fun of(
            workspaceId: String,
            apiKey: String,
            status: ApiKeyStatus = ApiKeyStatus.ENABLED,
            description: String,
        ) = ApiKeyEntity(
            apiKey = apiKey,
            workspaceId = workspaceId,
            description = description,
            status = status,
            auditingTime = AuditingTimeEntity.created(),
        )

        fun from(workspaceApiKeyEntity: WorkspaceApiKeyEntity) = ApiKeyEntity(
            apiKey = workspaceApiKeyEntity.key.apiKey,
            workspaceId = workspaceApiKeyEntity.key.workspaceId,
            status = workspaceApiKeyEntity.status,
            description = workspaceApiKeyEntity.description,
            auditingTime = AuditingTimeEntity.created(),
        )
    }

}
