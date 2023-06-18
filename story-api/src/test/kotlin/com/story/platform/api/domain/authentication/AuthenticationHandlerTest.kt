package com.story.platform.api.domain.authentication

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.core.common.enums.HttpHeaderType
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.error.NotFoundException
import com.story.platform.core.common.error.UnAuthorizedException
import com.story.platform.core.domain.authentication.AuthenticationKeyRetriever
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
import com.story.platform.core.domain.authentication.AuthenticationResponse
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange

class AuthenticationHandlerTest(
    @MockkBean
    private val authenticationKeyRetriever: AuthenticationKeyRetriever,
) : FunSpec({

    val authenticationHandler = AuthenticationHandler(authenticationKeyRetriever = authenticationKeyRetriever)

    context("인증 정보를 확인한다") {
        test("활성화되어 있는 인증 키인 경우 인증 체크를 통과한다") {
            val apiKey = "api-key"

            // given
            coEvery { authenticationKeyRetriever.getAuthenticationKey(apiKey) } returns AuthenticationResponse(
                serviceType = ServiceType.TWEETER,
                apiKey = apiKey,
                status = AuthenticationKeyStatus.ENABLED,
            )

            // when
            val sut = authenticationHandler.handleAuthentication(
                serverWebExchange = MockServerWebExchange.from(
                    MockServerHttpRequest.get("/test")
                        .header(HttpHeaderType.X_STORY_API_KEY.header, apiKey)
                )
            )

            // then
            sut.serviceType shouldBe ServiceType.TWEETER
            sut.apiKey shouldBe apiKey
            sut.status shouldBe AuthenticationKeyStatus.ENABLED
        }

        test("비활성화되어 있는 인증 키인 경우 인증에 실패한다") {
            // given
            val apiKey = "api-key"
            coEvery { authenticationKeyRetriever.getAuthenticationKey(apiKey) } returns AuthenticationResponse(
                serviceType = ServiceType.TWEETER,
                apiKey = apiKey,
                status = AuthenticationKeyStatus.DISABLED,
            )

            // when & then
            shouldThrowExactly<UnAuthorizedException> {
                authenticationHandler.handleAuthentication(
                    serverWebExchange = MockServerWebExchange.from(
                        MockServerHttpRequest.get("/test")
                            .header(HttpHeaderType.X_STORY_API_KEY.header, apiKey)
                    )
                )
            }
        }

        test("등록되지 않은 API-Key 인 경우 인증에 실패한다") {
            // given
            val apiKey = "api-key"
            coEvery { authenticationKeyRetriever.getAuthenticationKey(apiKey) } throws NotFoundException("등록되지 않은 인증 키 입니다")

            // when & then
            shouldThrowExactly<UnAuthorizedException> {
                authenticationHandler.handleAuthentication(
                    serverWebExchange = MockServerWebExchange.from(
                        MockServerHttpRequest.get("/test")
                            .header(HttpHeaderType.X_STORY_API_KEY.header, apiKey)
                    )
                )
            }
        }

        test("인증 헤더가 비어있는 경우 인증 키 조회 전에 인증에 실패한다") {
            // when & then
            shouldThrowExactly<UnAuthorizedException> {
                authenticationHandler.handleAuthentication(
                    serverWebExchange = MockServerWebExchange.from(
                        MockServerHttpRequest.get("/test")
                    )
                )
            }
        }
    }

})
