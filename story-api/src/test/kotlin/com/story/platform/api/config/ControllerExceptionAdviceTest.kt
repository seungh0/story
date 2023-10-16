package com.story.platform.api.config

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.domain.AvailabilityCheckApi
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.isFalse
import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.InternalServerException
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.component.ComponentAlreadyExistsException
import com.story.platform.core.infrastructure.spring.AvailabilityChecker
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

@ApiTest(
    AvailabilityCheckApi::class,
    ControllerExceptionAdvice::class,
)
internal class ControllerExceptionAdviceTest(
    private val webClient: WebTestClient,

    @MockkBean
    private val applicationAvailability: AvailabilityChecker,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,

    @MockkBean
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) : FunSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "story",
            authenticationKey = "api-key",
            status = AuthenticationStatus.ENABLED,
            description = ""
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    test("잘못된 HTTP Method로 요청이 들어온경우 Method_Not_Allowed를 반환한다") {
        // when
        val exchange = webClient.post()
            .uri("/health/liveness")
            .exchange()

        // then
        exchange.expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
    }

    test("지정된 에러가 발생하면, 해당 에러에 맞는 Http Status Code를 반환한다") {
        // given
        coEvery { applicationAvailability.livenessCheck() } throws ComponentAlreadyExistsException(message = "이미 존재하는 컴포넌트 입니다")

        // when
        val exchange = webClient.get()
            .uri("/health/liveness")
            .exchange()

        // then
        exchange.expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.ok").isFalse()
            .jsonPath("$.error").isEqualTo(ErrorCode.E409_ALREADY_EXISTS_COMPONENT.code)
    }

    test("알 수 없는 에러가 발생하는 경우, INTERNAL_SERVER_ERROR 를 반환한다") {
        // given
        coEvery { applicationAvailability.livenessCheck() } throws InternalServerException(
            message = "서버 내부에서 에러가 발생하였습니다"
        )

        // when
        val exchange = webClient.get()
            .uri("/health/liveness")
            .exchange()

        // then
        exchange.expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
            .expectBody()
            .jsonPath("$.ok").isFalse()
            .jsonPath("$.error").isEqualTo(ErrorCode.E500_INTERNAL_ERROR.code)
    }

})
