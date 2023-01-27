package com.story.platform.core

import com.story.platform.core.lib.EmbeddedCassandraRandomPortPropertiesInitializer
import com.story.platform.core.lib.SharedEmbeddedCassandra
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@Tag("integration-test")
@SpringBootTest
@ContextConfiguration(initializers = [EmbeddedCassandraRandomPortPropertiesInitializer::class])
internal abstract class IntegrationTest : CoreTestBase() {

    @AfterEach
    fun cleanUp() {

    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun setupCassandra() {
            SharedEmbeddedCassandra.start()
        }

        @AfterAll
        @JvmStatic
        fun cleanUpCassandra() {
            SharedEmbeddedCassandra.stop()
        }
    }

}
