package com.story.platform.core.common.json

import com.fasterxml.jackson.core.type.TypeReference

fun <T> T.toJson(): String = JsonUtils.toJson(this)
fun <T> String.toObject(toClass: Class<T>): T? = JsonUtils.toObject(input = this, toClass = toClass)
fun <T> String.toObject(typeReference: TypeReference<T>): T? =
    JsonUtils.toObject(input = this, typeReference = typeReference)
