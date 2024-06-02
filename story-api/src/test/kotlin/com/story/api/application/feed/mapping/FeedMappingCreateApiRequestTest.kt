package com.story.api.application.feed.mapping

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import jakarta.validation.Validation
import jakarta.validation.Validator
import java.time.Duration

class FeedMappingCreateApiRequestTest : StringSpec({

    val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    "30일" {
        print(Duration.ofDays(30).toString())
    }

    "피드 매핑에 대한 설명이 비어있을 수 있다" {
        // given
        val request = FeedMappingCreateRequest(description = "", retention = Duration.ofDays(30))

        // when
        val sut = validator.validate(request)

        // then
        sut shouldHaveSize 0
    }

    "피드 매핑에 대한 설명은 최대 300자까지만 사용할 수 있다" {
        // given
        val request = FeedMappingCreateRequest(description = "1".repeat(n = 300), retention = Duration.ofDays(30))

        // when
        val sut = validator.validate(request)

        // then
        sut shouldHaveSize 0
    }

    "피드 매핑에 대한 설명은 301자 이상인 경우 사용할 수 없다" {
        // given
        val request = FeedMappingCreateRequest(description = "1".repeat(n = 301), retention = Duration.ofDays(30))

        // when
        val sut = validator.validate(request)

        sut shouldHaveSize 1
    }

})
