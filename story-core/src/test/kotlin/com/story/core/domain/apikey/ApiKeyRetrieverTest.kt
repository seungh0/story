package com.story.core.domain.apikey

import com.story.core.IntegrationTest
import com.story.core.StringSpecIntegrationTest
import com.story.core.common.json.toJson
import com.story.core.common.json.toObject
import com.story.core.domain.apikey.storage.ApiKeyCassandraRepository
import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cache.GlobalCacheRepository
import io.kotest.matchers.shouldBe
import kotlin.jvm.optionals.getOrNull

@IntegrationTest
class ApiKeyRetrieverTest(
    private val apiKeyRetriever: ApiKeyRetriever,
    private val apiKeyRepository: ApiKeyCassandraRepository,
    private val globalCacheRepository: GlobalCacheRepository,
) : StringSpecIntegrationTest({

    "DB에서 API-KEY를 조회합니다" {
        // given
        val apiKey = ApiKeyFixture.create()
        apiKeyRepository.save(apiKey)

        // when
        val sut = apiKeyRetriever.getApiKey(apiKey = apiKey.apiKey).getOrNull()

        // then
        sut!!.workspaceId shouldBe apiKey.workspaceId
        sut.status shouldBe apiKey.status
        sut.description shouldBe apiKey.description
    }

    "DB에서 API-KEY를 조회하면 글로벌 캐시에 저장합니다" {
        // given
        val apiKey = ApiKeyFixture.create()
        apiKeyRepository.save(apiKey)

        apiKeyRetriever.getApiKey(apiKey = apiKey.apiKey).getOrNull()

        // when
        val sut = globalCacheRepository.getCache(
            cacheType = CacheType.API_KEY,
            cacheKey = "apiKey:${apiKey.apiKey}",
        )!!.toObject(ApiKey::class.java)

        sut!!.workspaceId shouldBe apiKey.workspaceId
        sut.status shouldBe apiKey.status
        sut.description shouldBe apiKey.description
    }

    "글로벌 캐시에 저장되어 있는 경우, 글로벌 캐시에서 가져온 API-Key가 조회됩니다" {
        val apiKey = ApiKeyFixture.create()
        globalCacheRepository.setCache(
            cacheType = CacheType.API_KEY,
            cacheKey = "apiKey:${apiKey.apiKey}",
            value = ApiKey.from(apiKey).toJson(),
        )

        // when
        val sut = apiKeyRetriever.getApiKey(apiKey = apiKey.apiKey).getOrNull()

        // then
        sut!!.workspaceId shouldBe apiKey.workspaceId
        sut.status shouldBe apiKey.status
        sut.description shouldBe apiKey.description
    }

})
