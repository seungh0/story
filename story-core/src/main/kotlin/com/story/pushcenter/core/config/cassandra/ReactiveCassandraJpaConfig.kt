package com.story.pushcenter.core.config.cassandra

import com.story.pushcenter.core.CoreRoot
import com.story.pushcenter.core.common.model.VersionReadConverter
import com.story.pushcenter.core.common.model.VersionWriteConverter
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
