package com.story.api

import com.ninjasquad.springmockk.MockkBean
import com.story.api.application.authentication.AuthenticationHandler
import com.story.api.application.workspace.WorkspaceRetrieveHandler
import com.story.core.domain.authentication.AuthenticationResponse
import com.story.core.domain.authentication.AuthenticationStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.mockk.coEvery

abstract class FunSpecDocsTest(body: FunSpec.() -> Unit = {}) : FunSpec() {

    @MockkBean
    private lateinit var authenticationHandler: AuthenticationHandler

    @MockkBean
    private lateinit var workspaceRetrieveHandler: WorkspaceRetrieveHandler

    override suspend fun beforeEach(testCase: TestCase) {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "story",
            authenticationKey = "api-key",
            status = AuthenticationStatus.ENABLED,
            description = "",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    init {
        body()
    }

}
