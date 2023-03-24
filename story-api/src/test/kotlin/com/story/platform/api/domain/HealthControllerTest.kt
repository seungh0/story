package com.story.platform.api.domain

import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.model.ApiResponse
import io.kotest.core.spec.style.FunSpec
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(HealthController::class)
internal class HealthControllerTest(
    private val webClient: WebTestClient,
) : FunSpec({

    test("Health Check API") {
        // when
        val exchange = webClient.get()
            .uri("/health")
            .headers(WebClientUtils.commonHeaders)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.result").isEqualTo(ApiResponse.OK.result!!)
    }

})
