package com.story.platform.core.domain.authentication

import com.story.platform.core.IntegrationTest
import com.story.platform.core.lib.TestCleaner
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
class AuthenticationCreatorTest(
    private val workspaceAuthenticationRepository: WorkspaceAuthenticationRepository,
    private val authenticationCreator: AuthenticationCreator,
    private val testCleaner: TestCleaner,
) : FunSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    context("신규 서비스 인증 키를 등록한다") {
        test("새로운 인증 키를 등록한다") {
            // given
            val workspaceId = "twitter"
            val apiKey = "api-key"
            val description = "트위터 피드 API Key"

            // when
            authenticationCreator.createAuthentication(
                workspaceId = workspaceId,
                authenticationKey = apiKey,
                description = description,
            )

            // then
            val authenticationKeys = workspaceAuthenticationRepository.findAll().toList()
            authenticationKeys shouldHaveSize 1
            authenticationKeys[0].also {
                it.key.workspaceId shouldBe workspaceId
                it.key.authenticationKey shouldBe apiKey
                it.description shouldBe description
                it.status shouldBe AuthenticationStatus.ENABLED
                it.auditingTime.createdAt shouldNotBe null
                it.auditingTime.updatedAt shouldBe it.auditingTime.createdAt
            }
        }

        test("해당 워크스페이스에 이미 등록되어 있는 인증 키인 경우, 중복 등록할 수 없다") {
            // given
            val authenticationKey = WorkspaceAuthenticationKeyFixture.create()
            workspaceAuthenticationRepository.save(authenticationKey)

            // when & then
            shouldThrowExactly<AuthenticationKeyAlreadyExistsException> {
                authenticationCreator.createAuthentication(
                    workspaceId = authenticationKey.key.workspaceId,
                    authenticationKey = authenticationKey.key.authenticationKey,
                    description = "",
                )
            }
        }

        test("다른 워크스페이스에 등록되어 있는 인증 키인 경우, 사용할 수 있다") {
            // given
            val authenticationKey = WorkspaceAuthenticationKeyFixture.create(
                workspaceId = "workspace-1"
            )
            workspaceAuthenticationRepository.save(authenticationKey)

            // when
            authenticationCreator.createAuthentication(
                workspaceId = "workspace-2",
                authenticationKey = authenticationKey.key.authenticationKey,
                description = "",
            )

            // then
            val authenticationKeys = workspaceAuthenticationRepository.findAll().toList()
            authenticationKeys shouldHaveSize 2

            authenticationKeys.map { it.key.workspaceId } shouldContainExactlyInAnyOrder listOf(
                "workspace-1",
                "workspace-2"
            )
            authenticationKeys.map { it.key.authenticationKey }
                .toSet() shouldBe setOf(authenticationKey.key.authenticationKey)
        }
    }

})
