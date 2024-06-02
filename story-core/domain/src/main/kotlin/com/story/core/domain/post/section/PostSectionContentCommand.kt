package com.story.core.domain.post.section

interface PostSectionContentCommand {

    val priority: Long

    fun sectionType(): PostSectionType

}
