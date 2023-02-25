package com.story.platform.core.support.json

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.story.platform.core.common.error.InternalServerException
import java.lang.reflect.Type

object JsonUtils {

    private val kotlinModule: KotlinModule = KotlinModule.Builder()
        .withReflectionCacheSize(512)
        .configure(KotlinFeature.NullToEmptyCollection, false)
        .configure(KotlinFeature.NullToEmptyMap, false)
        .configure(KotlinFeature.NullIsSameAsDefault, false)
        .configure(KotlinFeature.SingletonSupport, false)
        .configure(KotlinFeature.StrictNullChecks, false)
        .build()

    val DEFAULT_OBJECT_MAPPER = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .registerModules(
            ParameterNamesModule(),
            kotlinModule
        )

    fun <T> toObject(input: String, toClass: Class<T>): T? {
        return try {
            DEFAULT_OBJECT_MAPPER.readValue(input, toClass)
        } catch (exception: Exception) {
            throw InternalServerException(
                String.format(
                    "역직렬화 중 에러가 발생하였습니다. input: (%s) toClass: (%s)",
                    input,
                    toClass.simpleName
                ), exception
            )
        }
    }

    fun <T> toJson(input: T): String {
        return try {
            DEFAULT_OBJECT_MAPPER.writeValueAsString(input)
        } catch (exception: Exception) {
            throw InternalServerException(String.format("직렬화 중 에러가 발생하였습니다. input: (%s)", input), exception)
        }
    }

    fun <T> toList(json: String, clazz: Class<T>): List<T> {
        return toList(toJsonNode(json), clazz)
    }

    private fun <T> toList(jsonNode: JsonNode, clazz: Class<T>): List<T> {
        return try {
            val listType: JavaType = DEFAULT_OBJECT_MAPPER.typeFactory.constructCollectionType(
                ArrayList::class.java, clazz
            )
            val reader: ObjectReader = DEFAULT_OBJECT_MAPPER.readerFor(listType)
            val listValue = reader.readValue<List<T>>(jsonNode)
            listValue ?: listOf()
        } catch (exception: Exception) {
            throw InternalServerException(String.format("List 직렬화 중 에러가 발생하였습니다. input: (%s)", jsonNode), exception)
        }
    }

    fun <T> toSet(json: String, clazz: Class<T>): Set<T> {
        return toSet(toJsonNode(json), clazz)
    }

    private fun <T> toSet(jsonNode: JsonNode, clazz: Class<T>): Set<T> {
        return try {
            val listType: JavaType = DEFAULT_OBJECT_MAPPER.typeFactory.constructCollectionType(
                HashSet::class.java, clazz
            )
            val reader: ObjectReader = DEFAULT_OBJECT_MAPPER.readerFor(listType)
            val setValue = reader.readValue<Set<T>>(jsonNode)
            setValue ?: setOf()
        } catch (exception: Exception) {
            throw InternalServerException(String.format("Set 직렬화 중 에러가 발생하였습니다. input: (%s)", jsonNode), exception)
        }
    }

    fun <K, V> toMap(json: String): Map<K, V> {
        return try {
            DEFAULT_OBJECT_MAPPER.readValue(
                json,
                object : TypeReference<LinkedHashMap<K, V>>() {})
        } catch (exception: Exception) {
            throw InternalServerException(String.format("Map 직렬화 중 에러가 발생하였습니다. input: (%s)", json), exception)
        }
    }

    fun toJsonNode(json: String): JsonNode {
        return try {
            DEFAULT_OBJECT_MAPPER.readTree(json)
        } catch (exception: Exception) {
            throw InternalServerException(String.format("JsonNode 직렬화 중 에러가 발생하였습니다. input: (%s)", json), exception)
        }
    }

    fun deserialize(returnType: Class<*>, actualType: Type = returnType, jsonString: String): Any? {
        if (String::class.java == returnType) {
            return jsonString
        }

        if (List::class.java.isAssignableFrom(returnType) && actualType is Class<*>) {
            return toList(jsonString, actualType)
        }

        if (Set::class.java.isAssignableFrom(returnType) && actualType is Class<*>) {
            return toSet(jsonString, actualType)
        }

        return toObject(jsonString, returnType)
    }

}
