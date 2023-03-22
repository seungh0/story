package com.story.platform.core.support.json

fun <T> T.toJson(): String = JsonUtils.toJson(this)
