package com.story.platform.core.domain.authentication

import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.error.ConflictException
import com.story.platform.core.common.error.NotFoundException
import com.story.platform.core.helper.TestCleaner
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
class AuthenticationKeyManagerTest(
    private val authenticationKeyRepository: AuthenticationKeyRepository,
    private val authenticationKeyManager: AuthenticationKeyManager,
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
            authenticationKeyManager.register(
                workspaceId = workspaceId,
                authenticationKey = apiKey,
                description = description,
            )

            // then
            val authenticationKeys = authenticationKeyRepository.findAll().toList()
            authenticationKeys shouldHaveSize 1
            authenticationKeys[0].also {
                it.key.workspaceId shouldBe workspaceId
                it.key.authenticationKey shouldBe apiKey
                it.description shouldBe description
                it.status shouldBe AuthenticationKeyStatus.ENABLED
                it.auditingTime.createdAt shouldNotBe null
                it.auditingTime.updatedAt shouldBe it.auditingTime.createdAt
            }
        }

        test("사용하는 서비스에 이미 등록되어 있는 API-Key인 경우, 중복 등록할 수 없다") {
            // given
            val authenticationKey = AuthenticationKeyFixture.create()
            authenticationKeyRepository.save(authenticationKey)

            // when & then
            shouldThrowExactly<ConflictException> {
                authenticationKeyManager.register(
                    workspaceId = authenticationKey.key.workspaceId,
                    authenticationKey = authenticationKey.key.authenticationKey,
                    description = "",
                )
            }
        }
    }

    context("등록되어 있는 서비스 키를 변경한다") {
        test("등록된 서비스 인증 사용처에 대한 설명을 변경한다") {
            // given
            val description = "사용처에 대한 설명"

            val authenticationKey = AuthenticationKeyFixture.create()
            authenticationKeyRepository.save(authenticationKey)

            // when
            authenticationKeyManager.modify(
                workspaceId = authenticationKey.key.workspaceId,
                authenticationKey = authenticationKey.key.authenticationKey,
                description = description,
                status = null,
            )

            // then
            val authenticationKeys = authenticationKeyRepository.findAll().toList()
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
            val authenticationKey = AuthenticationKeyFixture.create(
                status = AuthenticationKeyStatus.ENABLED,
            )
            authenticationKeyRepository.save(authenticationKey)

            // when
            authenticationKeyManager.modify(
                workspaceId = authenticationKey.key.workspaceId,
                authenticationKey = authenticationKey.key.authenticationKey,
                description = null,
                status = AuthenticationKeyStatus.DISABLED,
            )

            // then
            val authenticationKeys = authenticationKeyRepository.findAll().toList()
            authenticationKeys shouldHaveSize 1
            authenticationKeys[0].also {
                it.key.workspaceId shouldBe authenticationKey.key.workspaceId
                it.key.authenticationKey shouldBe authenticationKey.key.authenticationKey
                it.description shouldBe authenticationKey.description
                it.status shouldBe AuthenticationKeyStatus.DISABLED
                it.auditingTime.createdAt shouldNotBe null
                it.auditingTime.updatedAt shouldNotBe null
                it.auditingTime.updatedAt shouldNotBe it.auditingTime.createdAt
            }
        }

        test("변경된 정보가 없는 경우 최근 수정일자가 변경되지 않는다") {
            // given
            val authenticationKey = AuthenticationKeyFixture.create(
                status = AuthenticationKeyStatus.ENABLED,
            )
            authenticationKeyRepository.save(authenticationKey)

            // when
            authenticationKeyManager.modify(
                workspaceId = authenticationKey.key.workspaceId,
                authenticationKey = authenticationKey.key.authenticationKey,
                description = authenticationKey.description,
                status = authenticationKey.status,
            )

            // then
            val authenticationKeys = authenticationKeyRepository.findAll().toList()
            authenticationKeys shouldHaveSize 1
            authenticationKeys[0].also {
                it.key.workspaceId shouldBe authenticationKey.key.workspaceId
                it.key.authenticationKey shouldBe authenticationKey.key.authenticationKey
                it.description shouldBe authenticationKey.description
                it.status shouldBe authenticationKey.status
                it.auditingTime.createdAt shouldNotBe null
                it.auditingTime.updatedAt shouldBe it.auditingTime.createdAt
            }
        }

        test("서비스에 등록되어 있지 않은 API-Key인 경우 변경할 수 없다") {
            // given
            val workspaceId = "twitter"

            // when & then
            shouldThrowExactly<NotFoundException> {
                authenticationKeyManager.modify(
                    workspaceId = workspaceId,
                    authenticationKey = "api-key",
                    description = "",
                    status = null,
                )
            }
        }

        test("API-Key는 각 서비스별로 독립적으로 관리된다") {
            // given
            val authenticationKey = AuthenticationKeyFixture.create(
                workspaceId = "twitter",
            )
            authenticationKeyRepository.save(authenticationKey)

            // when & then
            shouldThrowExactly<NotFoundException> {
                authenticationKeyManager.modify(
                    workspaceId = "instagram",
                    authenticationKey = authenticationKey.key.authenticationKey,
                    description = "",
                    status = null,
                )
            }
        }
    }

})
