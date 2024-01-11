package com.story.platform.api.application

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.application.authentication.AuthenticationHandler
import com.story.platform.api.application.component.ComponentCheckHandler
import com.story.platform.api.application.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.api.lib.isTrue
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.infrastructure.spring.AvailabilityChecker
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
    private val componentCheckHandler: ComponentCheckHandler,

    @MockkBean
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) : FunSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "story",
            authenticationKey = "api-key",
            status = AuthenticationStatus.ENABLED,
            description = "",
        )

        coEvery { componentCheckHandler.checkExistsComponent(any(), any(), any()) } returns Unit
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
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
            .jsonPath("$.ok").isTrue()
    }

})