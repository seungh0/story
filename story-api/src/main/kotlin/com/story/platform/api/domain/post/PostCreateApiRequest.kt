package com.story.platform.api.domain.post

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PostCreateApiRequest(
    @field:Valid
    val writer: PostWriterCreateApiRequest,

    @field:NotBlank
    @field:Size(max = 100)
    val title: String = "",

    @field:Size(max = 500)
    val content: String = "",
)
