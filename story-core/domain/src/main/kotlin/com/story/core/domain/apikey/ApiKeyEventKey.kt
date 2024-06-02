package com.story.core.domain.apikey

import com.story.core.domain.event.EventKey

data class ApiKeyEventKey(
    val apiKey: String,
) : EventKey {

    override fun makeKey(): String = "api-key::$apiKey"

}
