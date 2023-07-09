package com.story.platform.core.common.json

fun <T> T.toJson(): String = JsonUtils.toJson(this)
