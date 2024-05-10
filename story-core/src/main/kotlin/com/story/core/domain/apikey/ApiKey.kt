package com.story.core.domain.apikey

data class ApiKey(
    val workspaceId: String,
    val status: ApiKeyStatus,
    val description: String,
) {

    fun isActivated() = this.status.isActivated

    companion object {
        fun of(workspaceApiKey: WorkspaceApiKey) = ApiKey(
            workspaceId = workspaceApiKey.key.workspaceId,
            status = workspaceApiKey.status,
            description = workspaceApiKey.description,
        )

        fun of(apiKey: ApiKeyEntity) = ApiKey(
            workspaceId = apiKey.workspaceId,
            status = apiKey.status,
            description = apiKey.description,
        )
    }

}
