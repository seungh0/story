package com.story.platform.core.common.spring

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

@Component
@Target(AnnotationTarget.CLASS)
annotation class EventProducer(
    @get:AliasFor(annotation = Component::class) val value: String = "",
)
