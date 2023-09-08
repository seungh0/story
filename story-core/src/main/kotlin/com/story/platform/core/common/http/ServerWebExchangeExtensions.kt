package com.story.platform.core.common.http

import org.springframework.web.server.ServerWebExchange

fun ServerWebExchange.getApiKey(): String? {
    return this.request.headers.getFirst(HttpHeader.X_STORY_API_KEY.header)
}

fun ServerWebExchange.getRequestId(): String? {
    return this.request.headers.getFirst(HttpHeader.X_REQUEST_ID.header)
}

fun ServerWebExchange.getNonce(): String? {
    return this.request.headers.getFirst(HttpHeader.X_STORY_NONCE.header)
}
