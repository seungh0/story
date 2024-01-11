package com.story.api.application.authentication

import com.ninjasquad.springmockk.MockkBean
import com.story.core.common.http.HttpHeader
import com.story.core.domain.authentication.AuthenticationKeyEmptyException
import com.story.core.domain.authentication.AuthenticationKeyInactivatedException
import com.story.core.domain.authentication.AuthenticationKeyInvalidException
import com.story.core.domain.authentication.AuthenticationKeyNotExistsException
import com.story.core.domain.authentication.AuthenticationResponse
import com.story.core.domain.authentication.AuthenticationRetriever
import com.story.core.domain.authentication.AuthenticationStatus
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange

class AuthenticationHandlerTest(
    @MockkBean
    private val authenticationRetriever: AuthenticationRetriever,
) : FunSpec({

    val authenticationHandler = AuthenticationHandler(authenticationRetriever = authenticationRetriever)

    context("인증 정보를 확인한다") {
        test("활성화되어 있는 인증 키인 경우 인증 체크를 통과한다") {
            val apiKey = "api-key"

            // given
            coEvery { authenticationRetriever.getAuthentication(apiKey) } returns AuthenticationResponse(
                workspaceId = "story",
                authenticationKey = apiKey,
                status = AuthenticationStatus.ENABLED,
                description = "",
            )

            // when
            val sut = authenticationHandler.handleAuthentication(
                serverWebExchange = MockServerWebExchange.from(
                    MockServerHttpRequest.get("/test")
                        .header(HttpHeader.X_STORY_API_KEY.header, apiKey)
                )
            )

            // then
            sut.workspaceId shouldBe "story"
            sut.authenticationKey shouldBe apiKey
            sut.status shouldBe AuthenticationStatus.ENABLED
        }

        test("비활성화되어 있는 인증 키인 경우 인증에 실패한다") {
            // given
            val apiKey = "api-key"
            coEvery { authenticationRetriever.getAuthentication(apiKey) } returns AuthenticationResponse(
                workspaceId = "story",
                authenticationKey = apiKey,
                status = AuthenticationStatus.DISABLED,
                description = "",
            )

            // when & then
            shouldThrowExactly<AuthenticationKeyInactivatedException> {
                authenticationHandler.handleAuthentication(
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
            coEvery { authenticationRetriever.getAuthentication(apiKey) } throws AuthenticationKeyNotExistsException(
                "등록되지 않은 인증 키 입니다"
            )

            // when & then
            shouldThrowExactly<AuthenticationKeyInvalidException> {
                authenticationHandler.handleAuthentication(
                    serverWebExchange = MockServerWebExchange.from(
                        MockServerHttpRequest.get("/test")
                            .header(HttpHeader.X_STORY_API_KEY.header, apiKey)
                    )
                )
            }
        }

        test("인증 헤더가 비어있는 경우 인증 키 조회 전에 인증에 실패한다") {
            // when & then
            shouldThrowExactly<AuthenticationKeyEmptyException> {
                authenticationHandler.handleAuthentication(
                    serverWebExchange = MockServerWebExchange.from(
                        MockServerHttpRequest.get("/test")
                    )
                )
            }
        }
    }

})
