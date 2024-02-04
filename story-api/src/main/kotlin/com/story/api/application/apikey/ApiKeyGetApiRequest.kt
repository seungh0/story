package com.story.api.application.apikey

import com.story.core.domain.apikey.ApiKeyStatus

data class ApiKeyGetApiRequest(
    val filterStatus: ApiKeyStatus?,
)
