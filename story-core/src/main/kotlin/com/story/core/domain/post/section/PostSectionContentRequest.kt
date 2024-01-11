package com.story.core.domain.post.section

interface PostSectionContentRequest {

    val priority: Long

    fun sectionType(): PostSectionType

    fun toSection(): PostSectionContent

}
