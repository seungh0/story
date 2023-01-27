package com.story.platform.api

import com.story.platform.api.lib.EmbeddedCassandraRandomPortPropertiesInitializer
import com.story.platform.api.lib.SharedEmbeddedCassandra
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@Tag("integration-test")
@SpringBootTest
@ContextConfiguration(initializers = [EmbeddedCassandraRandomPortPropertiesInitializer::class])
internal abstract class IntegrationTest : ApiTestBase() {

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
