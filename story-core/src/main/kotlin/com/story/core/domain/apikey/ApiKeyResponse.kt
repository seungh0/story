package com.story.core.domain.apikey

data class ApiKeyResponse(
    val exists: Boolean,
    val workspaceId: String,
    val status: ApiKeyStatus,
    val description: String,
) {

    fun isActivated() = this.status.isActivated
    fun isNotFound() = !this.exists

    companion object {
        val notExist = ApiKeyResponse(
            exists = false,
            workspaceId = "",
            status = ApiKeyStatus.DISABLED,
            description = ""
        )

        fun of(workspaceApiKey: WorkspaceApiKey) = ApiKeyResponse(
            workspaceId = workspaceApiKey.key.workspaceId,
            status = workspaceApiKey.status,
            description = workspaceApiKey.description,
            exists = true,
        )

        fun of(apiKey: ApiKey) = ApiKeyResponse(
            workspaceId = apiKey.workspaceId,
            status = apiKey.status,
            description = apiKey.description,
            exists = true,
        )
    }

}
