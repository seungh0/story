package com.story.platform.api

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
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
