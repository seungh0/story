package com.story.platform.core

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@ComponentScan(basePackageClasses = [CoreRoot::class])
@Configuration
interface CoreRoot
