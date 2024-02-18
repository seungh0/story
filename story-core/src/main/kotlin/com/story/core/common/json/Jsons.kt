package com.story.core.common.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.story.core.common.error.InternalServerException
import com.story.core.domain.post.PostKey
import com.story.core.domain.post.PostKeyJsonDeserializer
import com.story.core.domain.post.PostKeyJsonSerializer

object Jsons {

    private val kotlinModule: KotlinModule = KotlinModule.Builder()
        .withReflectionCacheSize(512)
        .configure(KotlinFeature.NullToEmptyCollection, false)
        .configure(KotlinFeature.NullToEmptyMap, false)
        .configure(KotlinFeature.NullIsSameAsDefault, false)
        .configure(KotlinFeature.SingletonSupport, false)
        .configure(KotlinFeature.StrictNullChecks, false)
        .build()

    val DEFAULT_OBJECT_MAPPER: ObjectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .registerModules(JavaTimeModule(), ParameterNamesModule(), kotlinModule)
        .registerModules(
            SimpleModule().apply {
                addSerializer(PostKey::class.java, PostKeyJsonSerializer())
                addDeserializer(PostKey::class.java, PostKeyJsonDeserializer())
            }
        )

    fun <T> toObject(input: String, typeReference: TypeReference<T>): T? {
        return try {
            DEFAULT_OBJECT_MAPPER.readValue(input, typeReference)
        } catch (exception: Exception) {
            throw InternalServerException(
                message = String.format(
                    "역직렬화 중 에러가 발생하였습니다. input: (%s) toClass: (%s)",
                    input,
                    typeReference
                ),
                cause = exception
            )
        }
    }

    fun <T> toObject(input: String, toClass: Class<T>): T? {
        return try {
            DEFAULT_OBJECT_MAPPER.readValue(input, toClass)
        } catch (exception: Exception) {
            throw InternalServerException(
                message = String.format(
                    "역직렬화 중 에러가 발생하였습니다. input: (%s) toClass: (%s)",
                    input,
                    toClass.simpleName
                ),
                cause = exception
            )
        }
    }

    fun <K, V, T> toObject(map: Map<K, V>, toClass: Class<T>): T? {
        return try {
            DEFAULT_OBJECT_MAPPER.convertValue(map, toClass)
        } catch (exception: Exception) {
            throw InternalServerException(
                message = String.format(
                    "역직렬화 중 에러가 발생하였습니다. map: (%s) toClass: (%s)", map,
                    toClass.simpleName
                ),
                cause = exception
            )
        }
    }

    fun <K, V, T> toObject(map: Map<K, V>, typeReference: TypeReference<T>): T? {
        return try {
            DEFAULT_OBJECT_MAPPER.convertValue(map, typeReference)
        } catch (exception: Exception) {
            throw InternalServerException(
                message = String.format(
                    "역직렬화 중 에러가 발생하였습니다. map: (%s) toClass: (%s)",
                    map,
                    typeReference
                ),
                cause = exception
            )
        }
    }

    fun <T> toJson(input: T): String {
        return try {
            DEFAULT_OBJECT_MAPPER.writeValueAsString(input)
        } catch (exception: Exception) {
            throw InternalServerException(message = "직렬화 중 에러가 발생하였습니다. input: ($input)", cause = exception)
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
            throw InternalServerException(message = "List 직렬화 중 에러가 발생하였습니다. input: ($jsonNode)", cause = exception)
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
            throw InternalServerException("Set 직렬화 중 에러가 발생하였습니다. input: ($jsonNode)", cause = exception)
        }
    }

    fun <K, V> toMap(json: String): Map<K, V> {
        return try {
            DEFAULT_OBJECT_MAPPER.readValue(
                json,
                object : TypeReference<LinkedHashMap<K, V>>() {}
            )
        } catch (exception: Exception) {
            throw InternalServerException("Map 직렬화 중 에러가 발생하였습니다. input: ($json)", cause = exception)
        }
    }

    private fun toJsonNode(json: String): JsonNode {
        return try {
            DEFAULT_OBJECT_MAPPER.readTree(json)
        } catch (exception: Exception) {
            throw InternalServerException("JsonNode 직렬화 중 에러가 발생하였습니다. input: ($json)", cause = exception)
        }
    }

}
