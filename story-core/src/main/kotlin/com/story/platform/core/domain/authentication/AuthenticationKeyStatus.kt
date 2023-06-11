package com.story.platform.core.domain.authentication

enum class AuthenticationKeyStatus(
    private val description: String,
) {

    ENABLED(description = "활성화 상태"),
    DISABLED(description = "비활성화 상태"),

}
