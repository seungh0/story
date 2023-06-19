package com.story.platform.api.config

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.domain.HealthController
import com.story.platform.core.common.AvailabilityChecker
import com.story.platform.core.common.error.ConflictException
import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.InternalServerException
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(
    HealthController::class,
    ControllerExceptionAdvice::class,
)
internal class ControllerExceptionAdviceTest(
    private val webClient: WebTestClient,

    @MockkBean
    private val applicationAvailability: AvailabilityChecker,
) : FunSpec({

    test("잘못된 HTTPMethod로 요청이 들어온경우 Method_Not_Allowed를 반환한다") {
        // when
        val exchange = webClient.post()
            .uri("/health/liveness")
            .exchange()

        // then
        exchange.expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
    }

    test("지정된 에러가 발생하면, 해당 에러에 맞는 Http Status Code를 반환한다") {
        // given
        coEvery { applicationAvailability.livenessCheck() } throws ConflictException(message = "중복이 발생하였습니다")

        // when
        val exchange = webClient.get()
            .uri("/health/liveness")
            .exchange()

        // then
        exchange.expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.code").isEqualTo("409000")
            .jsonPath("$.message").isEqualTo(ErrorCode.E409_CONFLICT.errorMessage)
    }

    test("알 수 없는 에러가 발생하는 경우, Internal Server를 반환한다") {
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
            .jsonPath("$.code").isEqualTo("500000")
            .jsonPath("$.message")
            .isEqualTo(ErrorCode.E500_INTERNAL_SERVER_ERROR.errorMessage)
    }

})
