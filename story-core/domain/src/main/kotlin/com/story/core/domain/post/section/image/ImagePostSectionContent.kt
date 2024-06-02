package com.story.core.domain.post.section.image

import com.story.core.domain.post.section.PostSectionContent
import com.story.core.domain.post.section.PostSectionType

data class ImagePostSectionContent(
    val domain: String,
    val path: String,
    val width: Int,
    val height: Int,
    val extra: Map<String, Any>,
) : PostSectionContent {

    override fun sectionType() = PostSectionType.IMAGE

    companion object {
        fun from(sectionContent: ImagePostSectionContentEntity, imageDomain: String) = ImagePostSectionContent(
            domain = imageDomain,
            path = sectionContent.path,
            width = sectionContent.width,
            height = sectionContent.height,
            extra = sectionContent.extra,
        )
    }

}
