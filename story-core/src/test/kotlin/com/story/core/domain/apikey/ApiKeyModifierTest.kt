package com.story.core.domain.apikey

import com.story.core.FunSpecIntegrationTest
import com.story.core.IntegrationTest
import com.story.core.domain.apikey.storage.WorkspaceApiKeyCassandraRepository
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
class ApiKeyModifierTest(
    private val workspaceApiKeyRepository: WorkspaceApiKeyCassandraRepository,
    private val apiKeyModifier: ApiKeyModifier,
) : FunSpecIntegrationTest({

    context("등록되어 있는 API-키를 변경한다") {
        test("등록된 API-키에 대한 설명을 변경한다") {
            // given
            val description = "사용처에 대한 설명"

            val apiKey = WorkspaceApiKeyFixture.create()
            workspaceApiKeyRepository.save(apiKey)

            // when
            apiKeyModifier.patchApiKey(
                workspaceId = apiKey.key.workspaceId,
                key = apiKey.key.apiKey,
                description = description,
                status = null,
            )

            // then
            val apiKeys = workspaceApiKeyRepository.findAll().toList()
            apiKeys shouldHaveSize 1
            apiKeys[0].also {
                it.key.workspaceId shouldBe apiKey.key.workspaceId
                it.key.apiKey shouldBe apiKey.key.apiKey
                it.description shouldBe description
                it.status shouldBe apiKey.status
                it.auditingTime.createdAt shouldNotBe null
                it.auditingTime.updatedAt shouldNotBe null
                it.auditingTime.updatedAt shouldNotBe it.auditingTime.createdAt
            }
        }

        test("서비스 API-키를 사용 중지한다") {
            // given
            val apiKey = WorkspaceApiKeyFixture.create(
                status = ApiKeyStatus.ENABLED,
            )
            workspaceApiKeyRepository.save(apiKey)

            // when
            apiKeyModifier.patchApiKey(
                workspaceId = apiKey.key.workspaceId,
                key = apiKey.key.apiKey,
                description = null,
                status = ApiKeyStatus.DISABLED,
            )

            // then
            val apiKeys = workspaceApiKeyRepository.findAll().toList()
            apiKeys shouldHaveSize 1
            apiKeys[0].also {
                it.key.workspaceId shouldBe apiKey.key.workspaceId
                it.key.apiKey shouldBe apiKey.key.apiKey
                it.description shouldBe apiKey.description
                it.status shouldBe ApiKeyStatus.DISABLED
                it.auditingTime.createdAt shouldNotBe null
                it.auditingTime.updatedAt shouldNotBe null
                it.auditingTime.updatedAt shouldNotBe it.auditingTime.createdAt
            }
        }

        test("워크스페이스에 등록되어 있지 않은 API-키인 경우 변경할 수 없다") {
            // given
            val workspaceId = "story"

            // when & then
            shouldThrowExactly<ApiKeyNotExistsException> {
                apiKeyModifier.patchApiKey(
                    workspaceId = workspaceId,
                    key = "api-key",
                    description = "",
                    status = null,
                )
            }
        }

        test("API-키는 각 워크스페이스별로 독립적으로 관리된다") {
            // given
            val apiKey = WorkspaceApiKeyFixture.create(
                workspaceId = "story",
            )
            workspaceApiKeyRepository.save(apiKey)

            // when & then
            shouldThrowExactly<ApiKeyNotExistsException> {
                apiKeyModifier.patchApiKey(
                    workspaceId = "instagram",
                    key = apiKey.key.apiKey,
                    description = "",
                    status = null,
                )
            }
        }
    }

})
