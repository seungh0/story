package com.story.core.domain.post.section.image

import com.story.core.domain.post.section.PostSectionContentEntity
import com.story.core.domain.post.section.PostSectionType

data class ImagePostSectionContentEntity(
    val path: String, // /store/v1
    val width: Int,
    val height: Int,
    val fileSize: Long,
    val extra: Map<String, Any>,
) : PostSectionContentEntity {

    override fun sectionType(): PostSectionType = PostSectionType.IMAGE

    fun toSectionContent(imageDomain: String) = ImagePostSectionContent(
        domain = imageDomain,
        path = this.path,
        width = this.width,
        height = this.height,
        extra = this.extra,
    )

}
