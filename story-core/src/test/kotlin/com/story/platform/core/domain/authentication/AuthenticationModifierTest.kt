package com.story.platform.core.domain.authentication

import com.story.platform.core.IntegrationTest
import com.story.platform.core.lib.TestCleaner
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
class AuthenticationModifierTest(
    private val workspaceAuthenticationRepository: WorkspaceAuthenticationRepository,
    private val authenticationModifier: AuthenticationModifier,
    private val testCleaner: TestCleaner,
) : FunSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    context("등록되어 있는 인증 키를 변경한다") {
        test("등록된 인증 키에 대한 설명을 변경한다") {
            // given
            val description = "사용처에 대한 설명"

            val authenticationKey = WorkspaceAuthenticationKeyFixture.create()
            workspaceAuthenticationRepository.save(authenticationKey)

            // when
            authenticationModifier.patchAuthentication(
                workspaceId = authenticationKey.key.workspaceId,
                authenticationKey = authenticationKey.key.authenticationKey,
                description = description,
                status = null,
            )

            // then
            val authenticationKeys = workspaceAuthenticationRepository.findAll().toList()
            authenticationKeys shouldHaveSize 1
            authenticationKeys[0].also {
                it.key.workspaceId shouldBe authenticationKey.key.workspaceId
                it.key.authenticationKey shouldBe authenticationKey.key.authenticationKey
                it.description shouldBe description
                it.status shouldBe authenticationKey.status
                it.auditingTime.createdAt shouldNotBe null
                it.auditingTime.updatedAt shouldNotBe null
                it.auditingTime.updatedAt shouldNotBe it.auditingTime.createdAt
            }
        }

        test("서비스 인증 키를 사용 중지한다") {
            // given
            val authenticationKey = WorkspaceAuthenticationKeyFixture.create(
                status = AuthenticationStatus.ENABLED,
            )
            workspaceAuthenticationRepository.save(authenticationKey)

            // when
            authenticationModifier.patchAuthentication(
                workspaceId = authenticationKey.key.workspaceId,
                authenticationKey = authenticationKey.key.authenticationKey,
                description = null,
                status = AuthenticationStatus.DISABLED,
            )

            // then
            val authenticationKeys = workspaceAuthenticationRepository.findAll().toList()
            authenticationKeys shouldHaveSize 1
            authenticationKeys[0].also {
                it.key.workspaceId shouldBe authenticationKey.key.workspaceId
                it.key.authenticationKey shouldBe authenticationKey.key.authenticationKey
                it.description shouldBe authenticationKey.description
                it.status shouldBe AuthenticationStatus.DISABLED
                it.auditingTime.createdAt shouldNotBe null
                it.auditingTime.updatedAt shouldNotBe null
                it.auditingTime.updatedAt shouldNotBe it.auditingTime.createdAt
            }
        }

        test("워크스페이스에 등록되어 있지 않은 인증키인 경우 변경할 수 없다") {
            // given
            val workspaceId = "story"

            // when & then
            shouldThrowExactly<AuthenticationKeyNotExistsException> {
                authenticationModifier.patchAuthentication(
                    workspaceId = workspaceId,
                    authenticationKey = "api-key",
                    description = "",
                    status = null,
                )
            }
        }

        test("인증 키는 각 워크스페이스별로 독립적으로 관리된다") {
            // given
            val authenticationKey = WorkspaceAuthenticationKeyFixture.create(
                workspaceId = "story",
            )
            workspaceAuthenticationRepository.save(authenticationKey)

            // when & then
            shouldThrowExactly<AuthenticationKeyNotExistsException> {
                authenticationModifier.patchAuthentication(
                    workspaceId = "instagram",
                    authenticationKey = authenticationKey.key.authenticationKey,
                    description = "",
                    status = null,
                )
            }
        }
    }

})
