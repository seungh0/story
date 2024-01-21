package com.story.core.domain.post.section

import com.fasterxml.jackson.core.type.TypeReference
import com.story.core.common.error.InvalidArgumentsException
import com.story.core.common.error.NotSupportedException
import com.story.core.common.json.Jsons
import com.story.core.domain.post.section.image.ImagePostSectionContent
import com.story.core.domain.post.section.image.ImagePostSectionContentRequest
import com.story.core.domain.post.section.image.ImagePostSectionContentResponse
import com.story.core.domain.post.section.text.TextPostSectionContent
import com.story.core.domain.post.section.text.TextPostSectionContentRequest
import com.story.core.domain.post.section.text.TextPostSectionContentResponse

enum class PostSectionType(
    private val description: String,
    val requestClass: TypeReference<out PostSectionContentRequest>,
    private val contentClass: TypeReference<out PostSectionContent>,
    val responseClass: (PostSectionContent) -> PostSectionContentResponse,
) {

    TEXT(
        description = "텍스트 섹션",
        requestClass = object : TypeReference<TextPostSectionContentRequest>() {},
        contentClass = object : TypeReference<TextPostSectionContent>() {},
        responseClass = { content ->
            TextPostSectionContentResponse.from(content as TextPostSectionContent)
        },
    ),
    IMAGE(
        description = "이미지 섹션",
        requestClass = object : TypeReference<ImagePostSectionContentRequest>() {},
        contentClass = object : TypeReference<ImagePostSectionContent>() {},
        responseClass = { content ->
            ImagePostSectionContentResponse.from(content as ImagePostSectionContent)
        },
    ),
    ;

    fun toTypedRequest(sectionMap: Map<String, Any>): PostSectionContentRequest {
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

    fun toTypedResponse(sectionData: String): PostSectionContentResponse {
        try {
            return responseClass.invoke(Jsons.toObject(sectionData, this.contentClass)!!)
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
