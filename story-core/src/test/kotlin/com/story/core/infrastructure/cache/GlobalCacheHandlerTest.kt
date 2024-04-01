package com.story.core.infrastructure.cache

import com.story.core.common.json.toJson
import com.story.core.domain.apikey.ApiKeyResponse
import com.story.core.domain.apikey.ApiKeyStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import java.util.Optional

class GlobalCacheHandlerTest : FunSpec({

    val globalCacheRepository = mockk<GlobalCacheRepository>()
    val coroutineGlobalCacheHandler = GlobalCacheHandler(globalCacheRepository)

    context("Get Cache") {

        test("글로벌 캐시로 부터 캐시를 가져온다") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
            val keyString = "key"
            val apiKey = Optional.of(
                ApiKeyResponse(
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
        }

        test("레디스에 캐싱이 안되어 있는 경우 null을 반환한다") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
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
        }

        test("글로벌 캐시가 PER 알고리즘으로 Early 갱신에 부합하는 경우 null을 반환한다") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
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
        }

    }

    context("Refresh Cache") {
        test("캐시를 갱신한다") {
            // given
            val cacheType = CacheType.API_KEY_REVERSE
            val keyString = "key"
            val apiKey = Optional.of(
                ApiKeyResponse(
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
    }

    context("Evict Cache") {
        // given
        val cacheType = CacheType.API_KEY_REVERSE
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

})
