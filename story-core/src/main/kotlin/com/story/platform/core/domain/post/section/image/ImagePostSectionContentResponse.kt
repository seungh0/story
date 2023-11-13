package com.story.platform.core.domain.post.section.image

import com.story.platform.core.common.error.InternalServerException
import com.story.platform.core.common.json.Jsons
import com.story.platform.core.domain.post.section.PostSectionContentResponse
import com.story.platform.core.domain.post.section.PostSectionType

data class ImagePostSectionContentResponse(
    val path: String,
    val width: Int,
    val height: Int,
    val fileSize: Long,
    val fileName: String,
) : PostSectionContentResponse {

    override fun sectionType() = PostSectionType.IMAGE

    companion object {
        fun fromContent(content: String): PostSectionContentResponse {
            try {
                return Jsons.toObject(content, ImagePostSectionContentResponse::class.java)!!
            } catch (exception: Exception) {
                throw InternalServerException("SectionContent를 파싱할 수 없습니다. content: $content clazz: ${this.javaClass.simpleName}")
            }
        }
    }

}
