package com.story.platform.api.domain.post

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PostWriterModifyApiRequest(
    @field:Size(max = 100)
    @field:NotBlank
    val accountId: String = "",
)
