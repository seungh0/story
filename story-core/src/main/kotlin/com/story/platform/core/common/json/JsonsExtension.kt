package com.story.platform.core.common.json

import com.fasterxml.jackson.core.type.TypeReference

fun <T> T.toJson(): String = Jsons.toJson(this)
fun <T> String.toObject(toClass: Class<T>): T? = Jsons.toObject(input = this, toClass = toClass)
fun <T> String.toObject(typeReference: TypeReference<T>): T? =
    Jsons.toObject(input = this, typeReference = typeReference)
