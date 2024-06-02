package com.story.api

import com.ninjasquad.springmockk.MockkBean
import com.story.api.application.apikey.ApiKeyHandler
import com.story.api.application.workspace.WorkspaceRetrieveHandler
import com.story.core.domain.apikey.ApiKey
import com.story.core.domain.apikey.ApiKeyStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.mockk.coEvery

abstract class FunSpecDocsTest(body: FunSpec.() -> Unit = {}) : FunSpec() {

    @MockkBean
    private lateinit var apiKeyHandler: ApiKeyHandler

    @MockkBean
    private lateinit var workspaceRetrieveHandler: WorkspaceRetrieveHandler

    override suspend fun beforeEach(testCase: TestCase) {
        coEvery { apiKeyHandler.handleApiKey(any()) } returns ApiKey(
            workspaceId = "story",
            status = ApiKeyStatus.ENABLED,
            description = "",
            apiKey = "api-key",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    init {
        body()
    }

}
