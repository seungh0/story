package com.story.core.domain.post.section.link

import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.domain.post.section.PostSectionType

data class LinkPostSectionContentRequest(
    override val priority: Long,
    val link: String,
    val extra: Map<String, Any> = emptyMap(),
) : PostSectionContentRequest {

    override fun sectionType(): PostSectionType = PostSectionType.LINK

}
