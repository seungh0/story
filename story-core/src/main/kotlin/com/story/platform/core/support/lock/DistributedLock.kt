package com.story.platform.core.support.lock

import org.intellij.lang.annotations.Language
import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
annotation class DistributeLock(
    val lockType: DistributedLockType,

    @Language(value = "SpEL")
    val key: String,

    val timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    val waitTime: Long = 3000L, // The maximum time to acquire the lock
    val leaseTime: Long = 5000L, // Lock will be released automatically after defined leaseTime interval.
)
