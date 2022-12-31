package com.story.pushcenter.api.common.controller

import com.story.pushcenter.api.ApiTestBase
import com.story.pushcenter.api.domain.HealthController
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
