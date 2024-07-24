package com.story.core.domain.apikey

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.util.UUID

class ApiKeyReaderTest : StringSpec({

    val apiKeyReadRepository = mockk<ApiKeyReadRepository>()
    val apiKeyReader = ApiKeyReader(apiKeyReadRepository)

    "API Key를 조회합니다" {
        // given
        val key = UUID.randomUUID().toString()
        val apiKey = ApiKeyTestFixture.create(apiKey = key)

        coEvery { apiKeyReadRepository.findById(key) } returns apiKey

        // when
        val sut = apiKeyReader.getApiKey(key)

        // then
        sut shouldBe apiKey
    }

    "등록된 API Key가 아닌경우 null을 반환합니다" {
        // given
        val key = UUID.randomUUID().toString()
        coEvery { apiKeyReadRepository.findById(key) } returns null

        // when
        val sut = apiKeyReader.getApiKey(key)

        // then
        sut shouldBe null
    }

})
