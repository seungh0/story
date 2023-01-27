package com.story.platform.api.lib

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

internal class EmbeddedCassandraRandomPortPropertiesInitializer :
    ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val port = SharedEmbeddedCassandra.getPort()
        TestPropertyValues.of(
            "spring.data.cassandra.port=$port"
        ).applyTo(applicationContext.environment)
    }

}
