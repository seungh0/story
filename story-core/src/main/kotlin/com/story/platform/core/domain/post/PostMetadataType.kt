package com.story.platform.core.domain.post

import com.fasterxml.jackson.core.type.TypeReference

enum class PostMetadataType(
    private val description: String,
    val defaultValue: Any?,
    val typedReference: TypeReference<out Any>,
) {

    HAS_CHILDREN(
        description = "자식 포스트들의 존재 여부",
        defaultValue = false,
        typedReference = object : TypeReference<Boolean>() {},
    ),
    ;

}
