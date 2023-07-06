package com.story.platform.core

import io.kotest.core.annotation.Tags
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Tags("integration-test")
@ActiveProfiles("test")
@SpringBootTest
annotation class IntegrationTest
