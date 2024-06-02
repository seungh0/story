package com.story.core.common.http

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange

class ServerWebExchangeExtensionsKtTest : FunSpec({

    test("HTTP 헤더에서 API-Key 정보를 가져온다") {
        // given
        val apiKey = "api-key"

        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/test")
                .header(HttpHeader.X_STORY_API_KEY.header, apiKey)
        )

        // when
        val sut = exchange.getApiKey()

        // then
        sut shouldBe apiKey
    }

    test("HTTP 헤더에 API-Key가 없는 경우 null이 넘어온다") {
        // given
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/test")
        )

        // when
        val sut = exchange.getApiKey()

        // then
        sut shouldBe null
    }

})
