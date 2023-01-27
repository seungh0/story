package com.story.platform.api

import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest

@Tag("integration-test")
@SpringBootTest
internal abstract class IntegrationTest : ApiTestBase()
