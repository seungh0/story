package com.story.core

import io.kotest.core.annotation.Tags
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Tags("integration-test")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
annotation class IntegrationTest
