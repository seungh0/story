package com.story.platform.api.lib

import com.story.platform.core.common.http.HttpHeaderType
import org.springframework.http.HttpHeaders
import java.util.UUID
import java.util.function.Consumer

object WebClientUtils {

    val commonHeaders = Consumer<HttpHeaders> { header ->
        header["X-Forwarded-For"] = "127.0.0.1"
        header["X-Request-Id"] = UUID.randomUUID().toString()
    }

    val authenticationHeader = Consumer<HttpHeaders> { header ->
        header[HttpHeaderType.X_STORY_API_KEY.header] = "API-KEY"
        header["X-Forwarded-For"] = "127.0.0.1"
        header["X-Request-Id"] = UUID.randomUUID().toString()
    }

}
