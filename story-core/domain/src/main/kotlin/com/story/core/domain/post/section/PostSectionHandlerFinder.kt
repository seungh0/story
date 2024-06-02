package com.story.core.domain.post.section

fun interface PostSectionHandlerFinder {

    operator fun get(sectionType: PostSectionType): PostSectionHandler

}
