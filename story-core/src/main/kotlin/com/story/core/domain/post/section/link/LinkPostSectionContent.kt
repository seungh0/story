package com.story.core.domain.post.section.link

import com.story.core.domain.post.section.PostSectionContent
import com.story.core.domain.post.section.PostSectionType

data class LinkPostSectionContent(
    val link: String,
    val extra: Map<String, Any>,
) : PostSectionContent {

    override fun sectionType(): PostSectionType = PostSectionType.LINK

}
