package com.story.core.common.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

@Component
@Target(AnnotationTarget.CLASS)
annotation class EventConsumer(
    @get:AliasFor(annotation = Component::class) val value: String = "",
)
