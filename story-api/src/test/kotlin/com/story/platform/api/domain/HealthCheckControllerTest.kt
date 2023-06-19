package com.story.platform.api.domain

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.AvailabilityChecker
import com.story.platform.core.common.model.ApiResponse
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.ResponseEntity
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(HealthController::class)
internal class HealthCheckControllerTest(
    private val webClient: WebTestClient,

    @MockkBean
    private val applicationAvailability: AvailabilityChecker,
) : FunSpec({

    test("Health Check API") {
        // given
        coEvery { applicationAvailability.livenessCheck() } returns ResponseEntity.ok(ApiResponse.OK)

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
