package com.story.core.domain.authentication

enum class AuthenticationStatus(
    private val description: String,
    val isActivated: Boolean,
) {

    ENABLED(description = "활성화 상태", isActivated = true),
    DISABLED(description = "비활성화 상태", isActivated = false),
    ;

}
