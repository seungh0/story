package com.story.core.domain.apikey

import com.story.core.domain.apikey.storage.ApiKeyEntity
import com.story.core.domain.apikey.storage.WorkspaceApiKey

data class ApiKey(
    val apiKey: String,
    val workspaceId: String,
    val status: ApiKeyStatus,
    val description: String,
) {

    fun isActivated() = this.status.isActivated

    companion object {
        fun from(workspaceApiKey: WorkspaceApiKey) = ApiKey(
            apiKey = workspaceApiKey.key.apiKey,
            workspaceId = workspaceApiKey.key.workspaceId,
            status = workspaceApiKey.status,
            description = workspaceApiKey.description,
        )

        fun from(apiKey: ApiKeyEntity) = ApiKey(
            apiKey = apiKey.apiKey,
            workspaceId = apiKey.workspaceId,
            status = apiKey.status,
            description = apiKey.description,
        )
    }

}
