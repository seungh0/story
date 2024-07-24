package com.story.core.domain.apikey

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.util.UUID

class ApiKeyCreatorTest : StringSpec({

    val apiKeyReadRepository = mockk<ApiKeyReadRepository>()
    val apiKeyWriteRepository = mockk<ApiKeyWriteRepository>(relaxed = true)

    val apiKeyCreator = ApiKeyCreator(
        apiKeyReadRepository = apiKeyReadRepository,
        apiKeyWriteRepository = apiKeyWriteRepository,
    )

    "신규 API Key를 생성합니다" {
        // given
        val workspaceId = "story"
        val apiKey = UUID.randomUUID().toString()
        val description = "스토리 API Key"

        coEvery {
            apiKeyReadRepository.existsById(
                workspaceId = workspaceId,
                apiKey = apiKey,
            )
        } returns false

        // when
        apiKeyCreator.createApiKey(
            workspaceId = workspaceId,
            apiKey = apiKey,
            description = description,
        )

        // then
        coVerify(exactly = 1) {
            apiKeyWriteRepository.create(
                workspaceId = workspaceId,
                key = apiKey,
                description = description,
            )
        }
    }

    "이미 등록된 API Key라면 등록에 실패합니다" {
        // given
        val workspaceId = "story"
        val apiKey = UUID.randomUUID().toString()
        val description = "스토리 API Key"

        coEvery {
            apiKeyReadRepository.existsById(
                workspaceId = workspaceId,
                apiKey = apiKey,
            )
        } returns true

        // when
        shouldThrowExactly<ApiKeyAlreadyExistsException> {
            apiKeyCreator.createApiKey(
                workspaceId = workspaceId,
                apiKey = apiKey,
                description = description,
            )
        }

        // then
        coVerify(exactly = 0) { apiKeyWriteRepository.create(any(), any(), any()) }
    }

})
