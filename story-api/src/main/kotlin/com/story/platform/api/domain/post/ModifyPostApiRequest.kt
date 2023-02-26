package com.story.platform.api.domain.post

import javax.validation.constraints.NotBlank

data class ModifyPostApiRequest(
    @field:NotBlank
    val accountId: String = "",

    @field:NotBlank
    val title: String = "",

    @field:NotBlank
    val content: String = "",

    val extraJson: String? = null,
)
