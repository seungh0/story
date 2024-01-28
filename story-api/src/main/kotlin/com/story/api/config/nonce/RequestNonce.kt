package com.story.api.config.nonce

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class RequestNonce(
    val required: Boolean = true,
)
