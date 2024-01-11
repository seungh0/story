package com.story.api.application.authentication

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import jakarta.validation.Validation
import jakarta.validation.Validator

class AuthenticationModifyApiRequestTest : StringSpec({

    val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    "인증 정보에 대한 설명이 비어있을 수 있다" {
        // given
        val request = AuthenticationModifyApiRequest(description = "")

        // when
        val sut = validator.validate(request)

        // then
        sut shouldHaveSize 0
    }

    "인증 정보에 대한 설명은 최대 300자까지만 사용할 수 있다" {
        // given
        val request = AuthenticationModifyApiRequest(description = "1".repeat(n = 300))

        // when
        val sut = validator.validate(request)

        // then
        sut shouldHaveSize 0
    }

    "인증 정보에 대한 설명은 301자 이상인 경우 사용할 수 없다" {
        // given
        val request = AuthenticationModifyApiRequest(description = "1".repeat(n = 301))

        // when
        val sut = validator.validate(request)

        sut shouldHaveSize 1
    }

})
