package com.story.core.common.http

import org.springframework.web.server.ServerWebExchange

fun ServerWebExchange.getApiKey(): String? {
    return this.request.headers.getFirst(HttpHeader.X_STORY_API_KEY.header)
}

fun ServerWebExchange.getRequestId(): String? {
    return this.request.headers.getFirst(HttpHeader.X_REQUEST_ID.header)
}

fun ServerWebExchange.getRequestUserId(): String? {
    return this.request.headers.getFirst(HttpHeader.X_STORY_REQUEST_USER_ID.header)
}
