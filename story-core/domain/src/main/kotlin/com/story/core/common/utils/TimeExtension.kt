package com.story.core.common.utils

import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toEpochMilli(): Long = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
