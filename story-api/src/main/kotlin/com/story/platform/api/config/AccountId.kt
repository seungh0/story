package com.story.platform.api.config

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class AccountId(
    val optional: Boolean = false,
)
