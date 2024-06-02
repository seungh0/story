package com.story.core.domain

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@ComponentScan(basePackageClasses = [CassandraDataRoot::class])
@Configuration
interface CassandraDataRoot
