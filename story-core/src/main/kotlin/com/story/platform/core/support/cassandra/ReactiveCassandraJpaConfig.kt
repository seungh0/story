package com.story.platform.core.support.cassandra

import com.story.platform.core.common.converter.VersionReadConverter
import com.story.platform.core.common.converter.VersionWriteConverter
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@Profile("!test")
@EntityScan(basePackageClasses = [com.story.platform.core.CoreRoot::class])
@EnableReactiveCassandraRepositories(basePackageClasses = [com.story.platform.core.CoreRoot::class])
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

}
