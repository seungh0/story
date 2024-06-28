package com.story.core.domain.post.section.link

import com.story.core.domain.post.section.PostSectionContentEntity
import com.story.core.domain.post.section.PostSectionType

data class LinkPostSectionContentEntity(
    val link: String,
    val extra: Map<String, Any>,
) : PostSectionContentEntity {

    override fun sectionType(): PostSectionType = PostSectionType.LINK

    fun toSectionContent() = LinkPostSectionContent(
        link = this.link,
        extra = this.extra,
    )

}
