package com.story.core.infrastructure.cassandra

import com.story.core.common.StoryPackageConst
import com.story.core.infrastructure.cassandra.converter.PostIdReadConverter
import com.story.core.infrastructure.cassandra.converter.PostIdWriteConverter
import com.story.core.infrastructure.cassandra.converter.VersionReadConverter
import com.story.core.infrastructure.cassandra.converter.VersionWriteConverter
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
@EntityScan(basePackages = [StoryPackageConst.BASE_PACKAGE]) // TODO: data-cassandra 모듈로 이관 후 CassandraDataRoot::class 변경
@EnableReactiveCassandraRepositories(basePackages = [StoryPackageConst.BASE_PACKAGE])
@Configuration
class ReactiveCassandraConfig {

    @Bean
    fun cassandraCustomConversions(
        versionWriteConverter: VersionWriteConverter,
        versionReadConverter: VersionReadConverter,
        postIdReadConverter: PostIdReadConverter,
        postKeyWriteConverter: PostIdWriteConverter,
    ) = CassandraCustomConversions(
        listOf(
            versionWriteConverter,
            versionReadConverter,
            postKeyWriteConverter,
            postIdReadConverter,
        )
    )

    @Bean
    fun cassandraMappingContext(cassandraManagedTypes: CassandraManagedTypes): CassandraMappingContext {
        val mappingContext = CassandraMappingContext()
        mappingContext.setNamingStrategy(NamingStrategy.SNAKE_CASE)
        return mappingContext
    }

}
