package com.story.core.domain.post.section.image

import com.story.core.domain.post.section.PostSectionContentResponse
import com.story.core.domain.post.section.PostSectionType

data class ImagePostSectionContentResponse(
    val domain: String,
    val path: String,
    val width: Int,
    val height: Int,
    val extra: Map<String, Any>,
) : PostSectionContentResponse {

    override fun sectionType() = PostSectionType.IMAGE

    companion object {
        fun from(sectionContent: ImagePostSectionContent, imageDomain: String) = ImagePostSectionContentResponse(
            domain = imageDomain,
            path = sectionContent.path,
            width = sectionContent.width,
            height = sectionContent.height,
            extra = sectionContent.extra,
        )
    }

}
