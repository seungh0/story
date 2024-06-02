package com.story.api.application.apikey

import com.ninjasquad.springmockk.MockkBean
import com.story.core.common.http.HttpHeader
import com.story.core.domain.apikey.ApiKey
import com.story.core.domain.apikey.ApiKeyEmptyException
import com.story.core.domain.apikey.ApiKeyInactivatedException
import com.story.core.domain.apikey.ApiKeyInvalidException
import com.story.core.domain.apikey.ApiKeyRetriever
import com.story.core.domain.apikey.ApiKeyStatus
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import java.util.Optional

class ApiKeyHandlerTest(
    @MockkBean
    private val apiKeyRetriever: ApiKeyRetriever,
) : FunSpec({

    val apiKeyHandler = ApiKeyHandler(apiKeyRetriever = apiKeyRetriever)

    context("인증 정보를 확인한다") {
        test("활성화되어 있는 API-Key인 경우 인증 체크를 통과한다") {
            val apiKey = "api-key"

            // given
            coEvery { apiKeyRetriever.getApiKey(apiKey) } returns Optional.of(
                ApiKey(
                    workspaceId = "story",
                    status = ApiKeyStatus.ENABLED,
                    description = "",
                    apiKey = "api-key",
                )
            )

            // when
            val sut = apiKeyHandler.handleApiKey(
                serverWebExchange = MockServerWebExchange.from(
                    MockServerHttpRequest.get("/test")
                        .header(HttpHeader.X_STORY_API_KEY.header, apiKey)
                )
            )

            // then
            sut.workspaceId shouldBe "story"
            sut.status shouldBe ApiKeyStatus.ENABLED
        }

        test("비활성화되어 있는 API-Key인 경우 인증에 실패한다") {
            // given
            val apiKey = "api-key"
            coEvery { apiKeyRetriever.getApiKey(apiKey) } returns Optional.of(
                ApiKey(
                    workspaceId = "story",
                    status = ApiKeyStatus.DISABLED,
                    description = "",
                    apiKey = "api-key"
                )
            )

            // when & then
            shouldThrowExactly<ApiKeyInactivatedException> {
                apiKeyHandler.handleApiKey(
                    serverWebExchange = MockServerWebExchange.from(
                        MockServerHttpRequest.get("/test")
                            .header(HttpHeader.X_STORY_API_KEY.header, apiKey)
                    )
                )
            }
        }

        test("등록되지 않은 API-Key 인 경우 인증에 실패한다") {
            // given
            val apiKey = "api-key"
            coEvery { apiKeyRetriever.getApiKey(apiKey) } returns Optional.empty()

            // when & then
            shouldThrowExactly<ApiKeyInvalidException> {
                apiKeyHandler.handleApiKey(
                    serverWebExchange = MockServerWebExchange.from(
                        MockServerHttpRequest.get("/test")
                            .header(HttpHeader.X_STORY_API_KEY.header, apiKey)
                    )
                )
            }
        }

        test("인증 헤더가 비어있는 경우 API-Key 조회 전에 인증에 실패한다") {
            // when & then
            shouldThrowExactly<ApiKeyEmptyException> {
                apiKeyHandler.handleApiKey(
                    serverWebExchange = MockServerWebExchange.from(
                        MockServerHttpRequest.get("/test")
                    )
                )
            }
        }
    }

})
