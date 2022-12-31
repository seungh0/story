package com.story.pushcenter.core.common.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule

object JsonUtils {

    fun <T> toJson(input: T): String {
        return try {
            DEFAULT_OBJECT_MAPPER.writeValueAsString(input)
        } catch (e: JsonProcessingException) {
            throw IllegalArgumentException("Can't to serialize message: ${e.message}")
        }
    }

    fun <T> toObject(jsonString: String?, toClass: Class<T>?): T {
        return try {
            DEFAULT_OBJECT_MAPPER.readValue(jsonString, toClass)
        } catch (e: JsonProcessingException) {
            throw IllegalArgumentException("Can't to deserialize message: ${e.message}")
        }
    }

    val DEFAULT_OBJECT_MAPPER: ObjectMapper = ObjectMapper().apply {
        registerModules(ParameterNamesModule(), Jdk8Module(), JavaTimeModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
        configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true)
        configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
    }

}
