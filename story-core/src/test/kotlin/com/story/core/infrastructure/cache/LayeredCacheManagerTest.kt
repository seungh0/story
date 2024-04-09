package com.story.core.infrastructure.cache

import com.story.core.domain.apikey.ApiKeyResponse
import com.story.core.domain.apikey.ApiKeyStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.util.Optional

internal class LayeredCacheManagerTest : FunSpec({

    val cacheManager = mockk<CacheManager>()
    val layeredCacheManager = LayeredCacheManager(cacheManager = cacheManager)

    context("Get Cache") {
        test("로컬 캐시에 대상 캐시가 있으면 해당 캐시를 반환한다") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
            val cacheKeyString = "cacheString"
            val apiKey = Optional.of(
                ApiKeyResponse(
                    workspaceId = "workspaceId",
                    status = ApiKeyStatus.ENABLED,
                    description = "설명"
                )
            )

            coEvery {
                cacheManager.getCache(
                    CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            } returns apiKey

            // when
            val result = layeredCacheManager.getCacheFromLayeredCache(
                cacheType = cacheType,
                cacheKey = cacheKeyString,
            )

            // then
            result shouldBe apiKey
        }

        test("로컬 캐시에 대상 캐시가 없으면 글로벌 캐시에서 캐시를 조회해서 있으면 해당 값을 반환한다") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
            val cacheKeyString = "cacheString"
            val apiKey = Optional.of(
                ApiKeyResponse(
                    workspaceId = "workspaceId",
                    status = ApiKeyStatus.ENABLED,
                    description = "설명"
                )
            )

            coEvery {
                cacheManager.getCache(
                    CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            } returns null

            coEvery {
                cacheManager.getCache(
                    CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            } returns apiKey

            coEvery {
                cacheManager.refreshCache(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                    value = apiKey,
                )
            } returns Unit

            // when
            val result = layeredCacheManager.getCacheFromLayeredCache(
                cacheType = cacheType,
                cacheKey = cacheKeyString,
            )

            // then
            result shouldBe apiKey
        }

        test("글로벌 캐시 조회시 에러가 발생하면 에러 없이 null을 반환한다 - 장애 대응") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
            val cacheKeyString = "cacheString"

            coEvery {
                cacheManager.getCache(
                    CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            } returns null

            coEvery {
                cacheManager.getCache(
                    CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            } throws RuntimeException("레디스에 에러가 발생하였습니다")

            // when
            val result = layeredCacheManager.getCacheFromLayeredCache(
                cacheType = cacheType,
                cacheKey = cacheKeyString,
            )

            // then
            result shouldBe null
        }

        test("글로벌 캐시에서 캐시를 조회하면 해당 값을 로컬 캐시에 갱신시킨다") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
            val cacheKeyString = "cacheString"
            val value = Optional.of(
                ApiKeyResponse(
                    workspaceId = "workspaceId",
                    status = ApiKeyStatus.ENABLED,
                    description = "설명"
                )
            )

            coEvery {
                cacheManager.getCache(
                    CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            } returns null

            coEvery {
                cacheManager.getCache(
                    CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            } returns value

            coEvery {
                cacheManager.refreshCache(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                    value = value,
                )
            } returns Unit

            // when
            layeredCacheManager.getCacheFromLayeredCache(
                cacheType = cacheType,
                cacheKey = cacheKeyString,
            )

            // then
            coVerify(exactly = 1) {
                cacheManager.refreshCache(
                    cacheStrategy = any(),
                    cacheType = any(),
                    cacheKey = any(),
                    value = any(),
                )
            }
            coVerify(exactly = 1) {
                cacheManager.refreshCache(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                    value = value,
                )
            }
        }
    }

    context("Refresh Cache") {
        test("LayeredCache를 갱신하면 로컬 캐시와 글로벌 캐시를 모두 갱신한다") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
            val cacheKeyString = "cacheString"
            val value = Optional.of(
                ApiKeyResponse(
                    workspaceId = "workspaceId",
                    status = ApiKeyStatus.ENABLED,
                    description = "설명"
                )
            )

            coEvery {
                cacheManager.refreshCache(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                    value = value,
                )
            } returns Unit

            coEvery {
                cacheManager.refreshCache(
                    cacheStrategy = CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                    value = value,
                )
            } returns Unit

            // when
            layeredCacheManager.refreshCacheLayeredCache(
                cacheType = cacheType,
                cacheKey = cacheKeyString,
                value = value
            )

            // then
            coVerify(exactly = 2) {
                cacheManager.refreshCache(
                    cacheStrategy = any(),
                    cacheType = any(),
                    cacheKey = any(),
                    value = any(),
                )
            }
            coVerify(exactly = 1) {
                cacheManager.refreshCache(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                    value = value,
                )
            }
            coVerify(exactly = 1) {
                cacheManager.refreshCache(
                    cacheStrategy = CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                    value = value,
                )
            }
        }

        test("LayeredCache를 갱신시 글로벌 캐시에 에러가 발생하면 에러 없이 로컬 캐시만 갱신된다") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
            val cacheKeyString = "cacheString"
            val value = Optional.of(
                ApiKeyResponse(
                    workspaceId = "workspaceId",
                    status = ApiKeyStatus.ENABLED,
                    description = "설명"
                )
            )

            coEvery {
                cacheManager.refreshCache(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                    value = value,
                )
            } returns Unit

            coEvery {
                cacheManager.refreshCache(
                    cacheStrategy = CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                    value = value,
                )
            } throws RuntimeException("레디스에 문제가 발생하였습니다")

            // when
            layeredCacheManager.refreshCacheLayeredCache(
                cacheType = cacheType,
                cacheKey = cacheKeyString,
                value = value
            )

            // then
            coVerify(exactly = 2) {
                cacheManager.refreshCache(
                    cacheStrategy = any(),
                    cacheType = any(),
                    cacheKey = any(),
                    value = any(),
                )
            }
            coVerify(exactly = 1) {
                cacheManager.refreshCache(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                    value = value,
                )
            }
            coVerify(exactly = 1) {
                cacheManager.refreshCache(
                    cacheStrategy = CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                    value = value,
                )
            }
        }
    }

    context("Evict Cache") {
        test("LayeredCache를 갱신하면 로컬 캐시와 글로벌 캐시를 모두 삭제한다") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
            val cacheKeyString = "cacheString"

            coEvery {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            } returns Unit

            coEvery {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            } returns Unit

            // when
            layeredCacheManager.evictCacheLayeredCache(
                cacheType = cacheType,
                cacheKey = cacheKeyString,
                targetCacheStrategies = setOf(CacheStrategy.LOCAL, CacheStrategy.GLOBAL)
            )

            // then
            coVerify(exactly = 2) {
                cacheManager.evict(
                    cacheStrategy = any(),
                    cacheType = any(),
                    cacheKey = any(),
                )
            }
            coVerify(exactly = 1) {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            }
            coVerify(exactly = 1) {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            }
        }

        test("LayeredCache를 삭제시 글로벌 캐시에 에러가 발생하면 에러 없이 로컬 캐시만 삭제한다") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
            val cacheKeyString = "cacheString"

            coEvery {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            } returns Unit

            coEvery {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            } throws RuntimeException("레디스에 에러가 발생하였습니다")

            // when
            layeredCacheManager.evictCacheLayeredCache(
                cacheType = cacheType,
                cacheKey = cacheKeyString,
                targetCacheStrategies = setOf(CacheStrategy.LOCAL, CacheStrategy.GLOBAL)
            )

            // then
            coVerify(exactly = 2) {
                cacheManager.evict(
                    cacheStrategy = any(),
                    cacheType = any(),
                    cacheKey = any(),
                )
            }
            coVerify(exactly = 1) {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            }
            coVerify(exactly = 1) {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            }
        }

        test("LayeredCache 갱신시 로컬 캐시만 대상으로 하면 로컬 캐시만 갱신된다") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
            val cacheKeyString = "cacheString"

            coEvery {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            } returns Unit

            // when
            layeredCacheManager.evictCacheLayeredCache(
                cacheType = cacheType,
                cacheKey = cacheKeyString,
                targetCacheStrategies = setOf(CacheStrategy.LOCAL)
            )

            // then
            coVerify(exactly = 1) {
                cacheManager.evict(
                    cacheStrategy = any(),
                    cacheType = any(),
                    cacheKey = any(),
                )
            }
            coVerify(exactly = 1) {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            }
            coVerify(exactly = 0) {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            }
        }

        test("LayeredCache 갱신시 글로벌 캐시만 대상으로 하면 글로벌 캐시만 갱신된다") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
            val cacheKeyString = "cacheString"

            coEvery {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            } returns Unit

            // when
            layeredCacheManager.evictCacheLayeredCache(
                cacheType = cacheType,
                cacheKey = cacheKeyString,
                targetCacheStrategies = setOf(CacheStrategy.GLOBAL)
            )

            // then
            coVerify(exactly = 1) {
                cacheManager.evict(
                    cacheStrategy = any(),
                    cacheType = any(),
                    cacheKey = any(),
                )
            }
            coVerify(exactly = 0) {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.LOCAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            }
            coVerify(exactly = 1) {
                cacheManager.evict(
                    cacheStrategy = CacheStrategy.GLOBAL,
                    cacheType = cacheType,
                    cacheKey = cacheKeyString,
                )
            }
        }

    }

})
