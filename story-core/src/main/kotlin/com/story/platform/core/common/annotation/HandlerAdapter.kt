package com.story.platform.core.common.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Service

@Service
@Target(AnnotationTarget.CLASS)
annotation class HandlerAdapter(
    @get:AliasFor(annotation = Service::class) val value: String = "",
)
