package com.story.api.application.apikey

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.apikey.ApiKeyModifier
import com.story.core.domain.apikey.ApiKeyStatus

@HandlerAdapter
class ApiKeyModifyHandler(
    private val apiKeyModifier: ApiKeyModifier,
) {

    suspend fun patchApiKey(
        workspaceId: String,
        apiKey: String,
        description: String?,
        status: ApiKeyStatus?,
    ) {
        apiKeyModifier.patchApiKey(
            workspaceId = workspaceId,
            key = apiKey,
            description = description,
            status = status,
        )
    }

}
