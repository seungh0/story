package com.story.api

import com.story.api.config.advice.ControllerExceptionAdvice
import com.story.api.config.auth.AuthContextMethodArgumentResolver
import com.story.api.config.nonce.NonceMethodArgumentResolver
import com.story.api.config.security.HttpSecurityConfig
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.ActiveProfiles
import kotlin.reflect.KClass

@ActiveProfiles("test")
@Import(
    AuthContextMethodArgumentResolver::class,
    HttpSecurityConfig::class,
    NonceMethodArgumentResolver::class,
    ControllerExceptionAdvice::class,
)
@WebFluxTest
annotation class ApiTest(
    @get:AliasFor(
        annotation = WebFluxTest::class,
        attribute = "value"
    ) vararg val value: KClass<*> = [],
)
