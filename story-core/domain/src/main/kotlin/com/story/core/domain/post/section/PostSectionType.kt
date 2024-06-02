package com.story.core.domain.post.section

import com.fasterxml.jackson.core.type.TypeReference
import com.story.core.common.error.InvalidArgumentsException
import com.story.core.common.error.NotSupportedException
import com.story.core.common.json.Jsons
import com.story.core.domain.post.section.image.ImagePostSectionContentCommand
import com.story.core.domain.post.section.image.ImagePostSectionContentEntity
import com.story.core.domain.post.section.link.LinkPostSectionContentCommand
import com.story.core.domain.post.section.link.LinkPostSectionContentEntity
import com.story.core.domain.post.section.text.TextPostSectionContentCommand
import com.story.core.domain.post.section.text.TextPostSectionContentEntity

enum class PostSectionType(
    private val description: String,
    val requestClass: TypeReference<out PostSectionContentCommand>,
    private val contentClass: TypeReference<out PostSectionContentEntity>,
) {

    TEXT(
        description = "텍스트 섹션",
        requestClass = object : TypeReference<TextPostSectionContentCommand>() {},
        contentClass = object : TypeReference<TextPostSectionContentEntity>() {},
    ),
    IMAGE(
        description = "이미지 섹션",
        requestClass = object : TypeReference<ImagePostSectionContentCommand>() {},
        contentClass = object : TypeReference<ImagePostSectionContentEntity>() {},
    ),
    LINK(
        description = "링크 섹션",
        requestClass = object : TypeReference<LinkPostSectionContentCommand>() {},
        contentClass = object : TypeReference<LinkPostSectionContentEntity>() {},
    ),
    ;

    fun toTypedRequest(sectionMap: Map<String, Any>): PostSectionContentCommand {
        try {
            return Jsons.toObject(sectionMap, this.requestClass)!!
        } catch (exception: Exception) {
            throw InvalidArgumentsException(
                message = "PostSectionContentCommand parse failed",
                cause = exception,
                reasons = listOf(
                    "invalid arguments"
                )
            )
        }
    }

    fun toContent(sectionData: String): PostSectionContentEntity {
        try {
            return Jsons.toObject(sectionData, this.contentClass)!!
        } catch (exception: Exception) {
            throw InvalidArgumentsException(
                message = "PostSectionContentCommand parse failed",
                cause = exception,
                reasons = listOf(
                    "invalid arguments"
                )
            )
        }
    }

    companion object {
        fun findByCode(code: String): PostSectionType {
            return entries.find { value -> value.name == code }
                ?: throw NotSupportedException("현재 지원하지 않는 SectionType($code)입니다")
        }
    }

}
