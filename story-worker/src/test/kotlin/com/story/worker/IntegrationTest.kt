package com.story.worker

import io.kotest.core.annotation.Tags
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Tags("integration-test")
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest
annotation class IntegrationTest
