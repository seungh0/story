package com.story.platform.core.infrastructure.cassandra

import com.story.platform.core.CoreRoot
import com.story.platform.core.infrastructure.cassandra.converter.VersionReadConverter
import com.story.platform.core.infrastructure.cassandra.converter.VersionWriteConverter
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.cassandra.CassandraManagedTypes
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext
import org.springframework.data.cassandra.core.mapping.NamingStrategy
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@Profile("!test")
@EntityScan(basePackageClasses = [CoreRoot::class])
@EnableReactiveCassandraRepositories(basePackageClasses = [CoreRoot::class])
@Configuration
class ReactiveCassandraJpaConfig {

    @Bean
    fun cassandraCustomConversions(
        versionWriteConverter: VersionWriteConverter,
        versionReadConverter: VersionReadConverter,
    ) = CassandraCustomConversions(
        listOf(
            versionWriteConverter,
            versionReadConverter,
        )
    )

    @Bean
    fun cassandraMappingContext(cassandraManagedTypes: CassandraManagedTypes): CassandraMappingContext {
        val mappingContext = CassandraMappingContext()
        mappingContext.setNamingStrategy(NamingStrategy.SNAKE_CASE)
        return mappingContext
    }

}
