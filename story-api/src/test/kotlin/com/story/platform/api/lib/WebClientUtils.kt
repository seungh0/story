package com.story.platform.api.lib

import org.springframework.http.HttpHeaders
import java.util.UUID
import java.util.function.Consumer

object WebClientUtils {

    val commonHeaders = Consumer<HttpHeaders> { header ->
        header["X-Forwarded-For"] = "127.0.0.1"
        header["X-Request-Id"] = UUID.randomUUID().toString()
    }

}
