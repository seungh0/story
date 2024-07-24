package com.story.core.domain.apikey

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.util.Optional
import java.util.UUID

class ApiKeyReaderWithCacheTest : StringSpec({

    val apiKeyReader = mockk<ApiKeyReader>()
    val apiKeyReaderWithCache = ApiKeyReaderWithCache(
        apiKeyReader = apiKeyReader,
    )

    "API Key를 조회합니다" {
        // given
        val key = UUID.randomUUID().toString()
        val apiKey = ApiKeyTestFixture.create(apiKey = key)
        coEvery { apiKeyReader.getApiKey(key) } returns apiKey

        // when
        val sut = apiKeyReaderWithCache.getApiKey(key)

        // then
        sut shouldBe Optional.of(apiKey)
    }

    "API Key를 조회합니다 - 존재하지 않는 경우 Null Object를 반환합니다" {
        // given
        val key = UUID.randomUUID().toString()
        coEvery { apiKeyReader.getApiKey(key) } returns null

        // when
        val sut = apiKeyReaderWithCache.getApiKey(key)

        // then
        sut shouldBe Optional.empty()
    }

})
