package com.story.api.application.apikey

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.apikey.ApiKeyEvent
import com.story.core.domain.apikey.ApiKeyEventProducer
import com.story.core.domain.apikey.ApiKeyModifier
import com.story.core.domain.apikey.ApiKeyStatus

@HandlerAdapter
class ApiKeyModifyHandler(
    private val apiKeyModifier: ApiKeyModifier,
    private val apiKeyEventProducer: ApiKeyEventProducer,
) {

    suspend fun patchApiKey(
        workspaceId: String,
        key: String,
        description: String?,
        status: ApiKeyStatus?,
    ) {
        val apiKey = apiKeyModifier.patchApiKey(
            workspaceId = workspaceId,
            key = key,
            description = description,
            status = status,
        )

        apiKeyEventProducer.publishEvent(
            apiKey = key,
            event = ApiKeyEvent.updated(apiKey),
        )
    }

}
