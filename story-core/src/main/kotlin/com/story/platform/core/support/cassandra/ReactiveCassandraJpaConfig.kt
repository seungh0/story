package com.story.platform.core.support.cassandra

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@EntityScan(basePackageClasses = [com.story.platform.core.CoreRoot::class])
@EnableReactiveCassandraRepositories(basePackageClasses = [com.story.platform.core.CoreRoot::class])
@Configuration
class ReactiveCassandraJpaConfig {

    @Bean
    fun cassandraCustomConversions(
        versionWriteConverter: com.story.platform.core.common.converter.VersionWriteConverter,
        versionReadConverter: com.story.platform.core.common.converter.VersionReadConverter,
    ) = CassandraCustomConversions(
        listOf(
            versionWriteConverter,
            versionReadConverter,
        )
    )

}
