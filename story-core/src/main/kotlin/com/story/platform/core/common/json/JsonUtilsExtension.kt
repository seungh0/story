package com.story.platform.core.common.json

fun <T> T.toJson(): String = JsonUtils.toJson(this)
fun <T> String.toObject(toClass: Class<T>): T? = JsonUtils.toObject(this, toClass)
