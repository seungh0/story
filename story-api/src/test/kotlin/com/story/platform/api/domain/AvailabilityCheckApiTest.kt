package com.story.platform.api.domain

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.component.ComponentHandler
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.AvailabilityChecker
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
import com.story.platform.core.domain.authentication.AuthenticationResponse
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.http.ResponseEntity
import org.springframework.test.web.reactive.server.WebTestClient

@ApiTest(AvailabilityCheckApi::class)
internal class AvailabilityCheckApiTest(
    private val webClient: WebTestClient,

    @MockkBean
    private val applicationAvailability: AvailabilityChecker,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,

    @MockkBean
    private val componentHandler: ComponentHandler,
) : FunSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "twitter",
            authenticationKey = "api-key",
            status = AuthenticationKeyStatus.ENABLED,
        )

        coEvery { componentHandler.validateComponent(any(), any(), any()) } returns Unit
    }

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
