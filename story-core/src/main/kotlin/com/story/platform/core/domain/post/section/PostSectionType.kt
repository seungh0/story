package com.story.platform.core.domain.post.section

import com.fasterxml.jackson.core.type.TypeReference
import com.story.platform.core.common.error.InvalidArgumentsException
import com.story.platform.core.common.error.NotSupportedException
import com.story.platform.core.common.json.Jsons
import com.story.platform.core.domain.post.section.image.ImagePostSectionContentRequest
import com.story.platform.core.domain.post.section.text.TextPostSectionContentRequest

enum class PostSectionType(
    private val description: String,
    val requestClass: TypeReference<out PostSectionContentRequest>,
) {

    TEXT(
        description = "텍스트 섹션",
        requestClass = object : TypeReference<TextPostSectionContentRequest>() {},
    ),
    IMAGE(
        description = "이미지 섹션",
        requestClass = object : TypeReference<ImagePostSectionContentRequest>() {}
    )
    ;

    fun toTypedRequestClass(sectionMap: Map<String, Any>): PostSectionContentRequest {
        try {
            return Jsons.toObject(sectionMap, this.requestClass)!!
        } catch (exception: Exception) {
            throw InvalidArgumentsException(
                message = "PostSectionContentRequest parse failed",
                cause = exception,
                reasons = listOf(
                    "invalid arguments"
                )
            )
        }
    }

    companion object {
        fun findByCode(code: String): PostSectionType {
            return values().find { value -> value.name == code }
                ?: throw NotSupportedException("현재 지원하지 않는 SectionType($code)입니다")
        }
    }

}
