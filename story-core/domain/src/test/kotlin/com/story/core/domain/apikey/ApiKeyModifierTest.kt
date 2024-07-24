package com.story.core.domain.apikey

import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.util.UUID

class ApiKeyModifierTest : StringSpec({

    val apiKeyWriteRepository = mockk<ApiKeyWriteRepository>()
    val apiKeyEventProducer = mockk<ApiKeyEventProducer>(relaxed = true)
    val apiKeyModifier = ApiKeyModifier(
        apiKeyWriteRepository = apiKeyWriteRepository,
        apiKeyEventProducer = apiKeyEventProducer,
    )

    "API Key를 수정합니다" {
        // given
        val workspaceId = "story"
        val key = UUID.randomUUID().toString()
        val description = "스토리 사용"
        val status = ApiKeyStatus.ENABLED

        coEvery {
            apiKeyWriteRepository.partialUpdate(
                workspaceId = workspaceId,
                key = key,
                description = description,
                status = status,
            )
        } returns ApiKeyTestFixture.create(
            apiKey = key,
            workspaceId = workspaceId,
            description = description,
            status = status,
        )

        // when
        apiKeyModifier.patchApiKey(
            workspaceId = workspaceId,
            key = key,
            description = description,
            status = status,
        )

        // then
        coVerify(exactly = 1) {
            apiKeyWriteRepository.partialUpdate(
                workspaceId = workspaceId,
                key = key,
                description = description,
                status = status,
            )
        }
    }

    "API Key를 수정하면 수정 이벤트를 발행합니다" {
        // given
        val workspaceId = "story"
        val key = UUID.randomUUID().toString()
        val description = "스토리 사용"
        val status = ApiKeyStatus.ENABLED

        coEvery {
            apiKeyWriteRepository.partialUpdate(
                workspaceId = workspaceId,
                key = key,
                description = description,
                status = status,
            )
        } returns ApiKeyTestFixture.create(
            apiKey = key,
            workspaceId = workspaceId,
            description = description,
            status = status,
        )

        // when
        apiKeyModifier.patchApiKey(
            workspaceId = workspaceId,
            key = key,
            description = description,
            status = status,
        )

        // then
        coVerify(exactly = 1) {
            apiKeyEventProducer.publishEvent(
                apiKey = key,
                event = any(),
            )
        }
    }

})
