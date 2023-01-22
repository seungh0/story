package com.story.datacenter.core.config.cassandra

import com.story.datacenter.core.CoreRoot
import com.story.datacenter.core.config.converter.VersionReadConverter
import com.story.datacenter.core.config.converter.VersionWriteConverter
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

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

}
