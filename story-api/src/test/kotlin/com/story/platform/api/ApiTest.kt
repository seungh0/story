package com.story.platform.api

import com.story.platform.api.config.auth.AuthContextMethodArgumentResolver
import com.story.platform.api.config.security.HttpSecurityConfig
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.ActiveProfiles
import kotlin.reflect.KClass

@ActiveProfiles("test")
@Import(
    AuthContextMethodArgumentResolver::class,
    HttpSecurityConfig::class,
)
@WebFluxTest
annotation class ApiTest(
    @get:AliasFor(
        annotation = WebFluxTest::class,
        attribute = "value"
    ) vararg val value: KClass<*> = [],
)
