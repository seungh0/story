package com.story.core.common.utils

fun <T, R> Collection<T>.mapToSet(transform: (T) -> R) = this.asSequence()
    .map(transform)
    .toSet()
