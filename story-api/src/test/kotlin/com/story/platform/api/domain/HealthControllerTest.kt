package com.story.platform.api.domain

import com.story.platform.api.ApiTestBase
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest

@WebFluxTest(HealthController::class)
internal class HealthControllerTest : ApiTestBase() {

    @Test
    fun `Health Check API Test`() {
        webClient.get()
            .uri("/health")
            .exchange()
            .expectStatus().isOk
    }

}
