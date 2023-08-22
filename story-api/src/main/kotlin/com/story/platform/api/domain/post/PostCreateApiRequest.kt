package com.story.platform.api.domain.post

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PostCreateApiRequest(
    @field:Size(max = 100)
    @field:NotBlank
    val accountId: String = "",

    @field:NotBlank
    @field:Size(max = 100)
    val title: String = "",

    @field:Size(max = 500)
    val content: String = "",

    @field:Size(max = 10)
    val extra: Map<String, String> = mapOf(),
)
