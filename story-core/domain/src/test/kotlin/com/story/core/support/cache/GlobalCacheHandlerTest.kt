package com.story.core.support.cache

import com.story.core.common.json.toJson
import com.story.core.domain.apikey.ApiKey
import com.story.core.domain.apikey.ApiKeyStatus
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.util.Optional

class GlobalCacheHandlerTest : FunSpec({

    val globalCacheRepository = mockk<GlobalCacheRepository>()
    val coroutineGlobalCacheHandler = GlobalCacheHandler(
        globalCacheRepository = globalCacheRepository,
    )

    context("Refresh Cache") {
        test("캐시를 갱신한다") {
            // given
            val cacheType = CacheType.API_KEY
            val keyString = "key"
            val apiKey = Optional.of(
                ApiKey(
                    workspaceId = "workspaceId",
                    status = ApiKeyStatus.ENABLED,
                    description = "설명",
                    apiKey = "api-key",
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
    }

})
