package com.story.platform.core.domain.post.section.text

import com.story.platform.core.common.error.InternalServerException
import com.story.platform.core.common.json.Jsons
import com.story.platform.core.domain.post.section.PostSectionContentResponse
import com.story.platform.core.domain.post.section.PostSectionType

data class TextPostSectionContentResponse(
    val content: String,
) : PostSectionContentResponse {

    override fun sectionType() = PostSectionType.TEXT

    companion object {
        fun fromContent(content: String): PostSectionContentResponse {
            try {
                return Jsons.toObject(content, TextPostSectionContentResponse::class.java)!!
            } catch (exception: Exception) {
                throw InternalServerException("SectionContent를 파싱할 수 없습니다. content: $content clazz:${this.javaClass.simpleName}")
            }
        }
    }

}
