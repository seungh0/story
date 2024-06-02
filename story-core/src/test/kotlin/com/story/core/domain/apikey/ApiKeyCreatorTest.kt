package com.story.core.domain.apikey

import com.story.core.FunSpecIntegrationTest
import com.story.core.IntegrationTest
import com.story.core.common.utils.mapToSet
import com.story.core.domain.apikey.storage.WorkspaceApiKeyCassandraRepository
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
class ApiKeyCreatorTest(
    private val workspaceApiKeyRepository: WorkspaceApiKeyCassandraRepository,
    private val apiKeyCreator: ApiKeyCreator,
) : FunSpecIntegrationTest({

    context("신규 서비스 API 키를 등록한다") {
        test("새로운 API 키를 등록한다") {
            // given
            val workspaceId = "story"
            val apiKey = "api-key"
            val description = "트위터 피드 API Key"

            // when
            apiKeyCreator.createApiKey(
                workspaceId = workspaceId,
                apiKey = apiKey,
                description = description,
            )

            // then
            val apiKeys = workspaceApiKeyRepository.findAll().toList()
            apiKeys shouldHaveSize 1
            apiKeys[0].also {
                it.key.workspaceId shouldBe workspaceId
                it.key.apiKey shouldBe apiKey
                it.description shouldBe description
                it.status shouldBe ApiKeyStatus.ENABLED
                it.auditingTime.createdAt shouldNotBe null
                it.auditingTime.updatedAt shouldBe it.auditingTime.createdAt
            }
        }

        test("해당 워크스페이스에 이미 등록되어 있는 API 키인 경우, 중복 등록할 수 없다") {
            // given
            val apiKey = WorkspaceApiKeyFixture.create()
            workspaceApiKeyRepository.save(apiKey)

            // when & then
            shouldThrowExactly<ApiKeyAlreadyExistsException> {
                apiKeyCreator.createApiKey(
                    workspaceId = apiKey.key.workspaceId,
                    apiKey = apiKey.key.apiKey,
                    description = "",
                )
            }
        }

        test("다른 워크스페이스에 등록되어 있는 API 키인 경우, 사용할 수 있다") {
            // given
            val workspaceApiKey = WorkspaceApiKeyFixture.create(
                workspaceId = "workspace-1"
            )
            workspaceApiKeyRepository.save(workspaceApiKey)

            // when
            apiKeyCreator.createApiKey(
                workspaceId = "workspace-2",
                apiKey = workspaceApiKey.key.apiKey,
                description = "",
            )

            // then
            val apiKeys = workspaceApiKeyRepository.findAll().toList()
            apiKeys shouldHaveSize 2

            apiKeys.map { it.key.workspaceId } shouldContainExactlyInAnyOrder listOf(
                "workspace-1",
                "workspace-2"
            )
            apiKeys.mapToSet { it.key.apiKey } shouldBe setOf(workspaceApiKey.key.apiKey)
        }
    }

})
