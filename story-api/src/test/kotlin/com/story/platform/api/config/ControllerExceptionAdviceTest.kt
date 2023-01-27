package com.story.platform.api.config

import com.ninjasquad.springmockk.SpykBean
import com.story.platform.api.ApiTestBase
import com.story.platform.api.domain.HealthController
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpStatus

@WebFluxTest(
    HealthController::class,
    ControllerExceptionAdvice::class
)
internal class ControllerExceptionAdviceTest : ApiTestBase() {

    @SpykBean
    private lateinit var healthCheckController: HealthController

    @Test
    fun `잘못된 HTTPMethod로 요청이 들어온경우 Method_Not_Allowed를 반환한다`() {
        // when & then
        webClient.post()
            .uri("/health")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
    }

    @Test
    fun `지정된 에러가 발생하면, 해당 에러에 맞는 Http Status Code를 반환한다`() {
        // given
        every { healthCheckController.health() } throws com.story.platform.core.common.error.ConflictException("중복이 발생하였습니다")

        // when & then
        webClient.get()
            .uri("/health")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.code").isEqualTo("409000")
            .jsonPath("$.message").isEqualTo(com.story.platform.core.common.error.ErrorCode.E409_CONFLICT.errorMessage)
    }

    @Test
    fun `알 수 없는 에러가 발생하는 경우, Interanl_Server를 반환한다`() {
        // given
        every { healthCheckController.health() } throws com.story.platform.core.common.error.InternalServerException(
            "서버 내부에서 에러가 발생하였습니다"
        )

        // when & then
        webClient.get()
            .uri("/health")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
            .expectBody()
            .jsonPath("$.code").isEqualTo("500000")
            .jsonPath("$.message")
            .isEqualTo(com.story.platform.core.common.error.ErrorCode.E500_INTERNAL_SERVER_ERROR.errorMessage)
    }

}
