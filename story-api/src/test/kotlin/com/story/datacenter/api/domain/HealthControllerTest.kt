package com.story.datacenter.api.domain

import com.story.datacenter.api.ApiTestBase
import com.story.datacenter.api.domain.HealthController
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(HealthController::class)
internal class HealthControllerTest(
    private val webClient: WebTestClient,
) : ApiTestBase() {

    @Test
    fun `Health Check API Test`() {
        webClient.get()
            .uri("/health")
            .exchange()
            .expectStatus().isOk
    }

}
