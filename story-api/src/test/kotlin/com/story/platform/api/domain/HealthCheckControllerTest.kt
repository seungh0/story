package com.story.platform.api.domain

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.model.ApiResponse
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import org.springframework.boot.availability.ApplicationAvailability
import org.springframework.boot.availability.LivenessState
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(HealthCheckController::class)
internal class HealthCheckControllerTest(
    private val webClient: WebTestClient,

    @MockkBean
    private val applicationAvailability: ApplicationAvailability,
) : FunSpec({

    test("Health Check API") {
        // given
        every { applicationAvailability.livenessState } returns LivenessState.CORRECT

        // when
        val exchange = webClient.get()
            .uri("/health/liveness")
            .headers(WebClientUtils.commonHeaders)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.result").isEqualTo(ApiResponse.OK.result!!)
    }

})
