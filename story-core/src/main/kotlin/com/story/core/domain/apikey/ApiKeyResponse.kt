package com.story.core.domain.apikey

data class ApiKeyResponse(
    val workspaceId: String,
    val status: ApiKeyStatus,
    val description: String,
) {

    fun isActivated() = this.status.isActivated

    companion object {
        fun of(workspaceApiKey: WorkspaceApiKey) = ApiKeyResponse(
            workspaceId = workspaceApiKey.key.workspaceId,
            status = workspaceApiKey.status,
            description = workspaceApiKey.description,
        )

        fun of(apiKey: ApiKey) = ApiKeyResponse(
            workspaceId = apiKey.workspaceId,
            status = apiKey.status,
            description = apiKey.description,
        )
    }

}
