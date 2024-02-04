package com.story.api.lib

import com.story.core.common.http.HttpHeader
import org.springframework.http.HttpHeaders
import java.util.UUID
import java.util.function.Consumer

object WebClientUtils {

    val commonHeaders = Consumer<HttpHeaders> { header ->
        header[HttpHeader.X_FORWARDED_FOR.header] = "127.0.0.1"
        header[HttpHeader.X_REQUEST_ID.header] = UUID.randomUUID().toString()
    }

    val apiKeyHeader = Consumer<HttpHeaders> { header ->
        header[HttpHeader.X_STORY_API_KEY.header] = "{{YOUR-STORY-API_KRY-KEY}}"
        header[HttpHeader.X_FORWARDED_FOR.header] = "127.0.0.1"
        header[HttpHeader.X_REQUEST_ID.header] = UUID.randomUUID().toString()
    }

    val apiKeyHeaderWithRequestUserId = Consumer<HttpHeaders> { header ->
        header[HttpHeader.X_STORY_API_KEY.header] = "{{YOUR-STORY-API_KRY-KEY}}"
        header[HttpHeader.X_FORWARDED_FOR.header] = "127.0.0.1"
        header[HttpHeader.X_REQUEST_ID.header] = UUID.randomUUID().toString()
        header[HttpHeader.X_STORY_REQUEST_USER_ID.header] = "request-user-id"
    }

}
