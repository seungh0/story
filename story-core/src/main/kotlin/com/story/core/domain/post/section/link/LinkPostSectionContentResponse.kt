package com.story.core.domain.post.section.link

import com.story.core.domain.post.section.PostSectionContentResponse
import com.story.core.domain.post.section.PostSectionType

data class LinkPostSectionContentResponse(
    val link: String,
    val extra: Map<String, Any>,
) : PostSectionContentResponse {

    override fun sectionType(): PostSectionType = PostSectionType.LINK

    companion object {
        fun from(sectionContent: LinkPostSectionContent) = LinkPostSectionContentResponse(
            link = sectionContent.link,
            extra = sectionContent.extra,
        )
    }

}
