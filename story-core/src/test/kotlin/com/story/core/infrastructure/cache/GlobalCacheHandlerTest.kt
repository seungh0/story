package com.story.core.infrastructure.cache

import com.story.core.common.error.InternalServerException
import com.story.core.common.json.toJson
import com.story.core.domain.apikey.ApiKey
import com.story.core.domain.apikey.ApiKeyStatus
import com.story.core.infrastrcture.curcuitbreaker.StubCircuitBreaker
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import java.util.Optional

class GlobalCacheHandlerTest : FunSpec({

    val globalCacheRepository = mockk<GlobalCacheRepository>()
    val circuitBreaker = StubCircuitBreaker(circuitOpen = false)
    val coroutineGlobalCacheHandler = GlobalCacheHandler(
        globalCacheRepository = globalCacheRepository,
        circuitBreaker = circuitBreaker,
    )

    context("Get Cache") {
        test("글로벌 캐시로 부터 캐시를 가져온다") {
            // given
            val cacheType = CacheType.API_KEY
            val keyString = "key"
            val apiKey = Optional.of(
                ApiKey(
                    workspaceId = "workspaceId",
                    status = ApiKeyStatus.ENABLED,
                    description = "설명"
                )
            )

            coEvery {
                globalCacheRepository.isEarlyRecomputedRequired(
                    cacheType = cacheType,
                    cacheKey = keyString,
                )
            } returns false
            coEvery {
                globalCacheRepository.getCache(
                    cacheType = cacheType,
                    cacheKey = keyString
                )
            } returns apiKey.toJson()

            // when
            val cache = coroutineGlobalCacheHandler.getCache(
                cacheType = cacheType,
                cacheKey = keyString,
            )

            // then
            cache shouldBe apiKey
            coVerify(exactly = 1) { globalCacheRepository.isEarlyRecomputedRequired(cacheType, keyString) }
            coVerify(exactly = 1) { globalCacheRepository.getCache(cacheType, keyString) }
        }

        test("레디스에 캐싱이 안되어 있는 경우 null을 반환한다") {
            // given
            val cacheType = CacheType.API_KEY
            val keyString = "key"

            coEvery {
                globalCacheRepository.isEarlyRecomputedRequired(
                    cacheType = cacheType,
                    cacheKey = keyString,
                )
            } returns false
            coEvery {
                globalCacheRepository.getCache(
                    cacheType = cacheType,
                    cacheKey = keyString
                )
            } returns null

            // when
            val cache = coroutineGlobalCacheHandler.getCache(
                cacheType = cacheType,
                cacheKey = keyString,
            )

            // then
            assertThat(cache).isNull()
            coVerify(exactly = 1) { globalCacheRepository.isEarlyRecomputedRequired(cacheType, keyString) }
            coVerify(exactly = 1) { globalCacheRepository.getCache(cacheType, keyString) }
        }

        test("글로벌 캐시가 PER 알고리즘으로 Early 갱신에 부합하는 경우 null을 반환한다") {
            // given
            val cacheType = CacheType.API_KEY
            val keyString = "key"

            coEvery {
                globalCacheRepository.isEarlyRecomputedRequired(
                    cacheType = cacheType,
                    cacheKey = keyString,
                )
            } returns true

            // when
            val cache = coroutineGlobalCacheHandler.getCache(
                cacheType = cacheType,
                cacheKey = keyString,
            )

            // then
            assertThat(cache).isNull()
            coVerify(exactly = 1) { globalCacheRepository.isEarlyRecomputedRequired(cacheType, keyString) }
            coVerify(exactly = 0) { globalCacheRepository.getCache(any(), any()) }
        }

        test("서킷이 오픈되어 있는 경우 Redis Call 없이 fallback으로 동작한다") {
            // given
            circuitBreaker.circuitOpen = true

            val cacheType = CacheType.API_KEY
            val keyString = "key"

            // when
            shouldThrowExactly<InternalServerException> {
                coroutineGlobalCacheHandler.getCache(
                    cacheType = cacheType,
                    cacheKey = keyString,
                )
            }

            // then
            coVerify(exactly = 0) { globalCacheRepository.isEarlyRecomputedRequired(any(), any()) }
            coVerify(exactly = 0) { globalCacheRepository.getCache(any(), any()) }
        }
    }

    context("Refresh Cache") {
        test("캐시를 갱신한다") {
            // given
            val cacheType = CacheType.API_KEY
            val keyString = "key"
            val apiKey = Optional.of(
                ApiKey(
                    workspaceId = "workspaceId",
                    status = ApiKeyStatus.ENABLED,
                    description = "설명"
                )
            )

            coEvery {
                globalCacheRepository.setCache(
                    cacheType = cacheType,
                    cacheKey = keyString,
                    value = any(),
                )
            } returns Unit

            // when
            coroutineGlobalCacheHandler.refresh(
                cacheType = cacheType,
                cacheKey = keyString,
                value = apiKey,
            )

            // then
            coVerify(exactly = 1) {
                globalCacheRepository.setCache(
                    cacheType = any(),
                    cacheKey = any(),
                    value = any(),
                )
            }

            coVerify(exactly = 1) {
                globalCacheRepository.setCache(
                    cacheType = cacheType,
                    cacheKey = keyString,
                    value = apiKey.toJson(),
                )
            }
        }

        test("서킷이 오픈되어 있는 경우 Redis call 없이 fast fail 한다") {
            // given
            circuitBreaker.circuitOpen = true

            val cacheType = CacheType.API_KEY
            val keyString = "key"
            val apiKey = Optional.of(
                ApiKey(
                    workspaceId = "workspaceId",
                    status = ApiKeyStatus.ENABLED,
                    description = "설명"
                )
            )

            // when
            shouldThrowExactly<InternalServerException> {
                coroutineGlobalCacheHandler.refresh(
                    cacheType = cacheType,
                    cacheKey = keyString,
                    value = apiKey,
                )
            }

            // then
            coVerify(exactly = 0) {
                globalCacheRepository.setCache(
                    cacheType = any(),
                    cacheKey = any(),
                    value = any(),
                )
            }
        }
    }

    context("Evict Cache") {
        test("캐시를 만료시킵니다") {
            // given
            val cacheType = CacheType.API_KEY
            val keyString = "key"

            coEvery { globalCacheRepository.evict(cacheType = cacheType, cacheKey = keyString) } returns Unit

            // when
            coroutineGlobalCacheHandler.evict(
                cacheType = cacheType,
                cacheKey = keyString,
            )

            // then
            coVerify(exactly = 1) {
                globalCacheRepository.evict(
                    cacheType = any(),
                    cacheKey = any(),
                )
            }
            coVerify(exactly = 1) {
                globalCacheRepository.evict(
                    cacheType = cacheType,
                    cacheKey = keyString,
                )
            }
        }

        test("서킷이 오픈되어 있는 경우 Redis call 하지 않고 fast-fail 합니다") {
            // given
            circuitBreaker.circuitOpen = true

            val cacheType = CacheType.API_KEY
            val keyString = "key"

            // when
            shouldThrowExactly<InternalServerException> {
                coroutineGlobalCacheHandler.evict(
                    cacheType = cacheType,
                    cacheKey = keyString,
                )
            }

            // then
            coVerify(exactly = 0) {
                globalCacheRepository.evict(
                    cacheType = any(),
                    cacheKey = any(),
                )
            }
        }
    }

})
