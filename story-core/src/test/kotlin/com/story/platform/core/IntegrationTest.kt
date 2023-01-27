package com.story.platform.core

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest

@Tag("integration-test")
@SpringBootTest
internal abstract class IntegrationTest : CoreTestBase() {

    @AfterEach
    fun cleanUp() {

    }

}
