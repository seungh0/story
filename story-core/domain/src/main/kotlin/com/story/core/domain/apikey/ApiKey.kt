package com.story.core.domain.apikey

data class ApiKey(
    val apiKey: String,
    val workspaceId: String,
    val status: ApiKeyStatus,
    val description: String,
) {

    fun isActivated() = this.status.isActivated

}
