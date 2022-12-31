package com.story.pushcenter.core.config.cassandra

import com.story.pushcenter.core.CoreRoot
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@EntityScan(basePackageClasses = [CoreRoot::class])
@EnableReactiveCassandraRepositories(basePackageClasses = [CoreRoot::class])
@Configuration
class ReactiveCassandraJpaConfig
