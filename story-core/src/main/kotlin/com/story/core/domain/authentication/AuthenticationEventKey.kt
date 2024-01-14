package com.story.core.domain.authentication

import com.story.core.domain.event.EventKey

data class AuthenticationEventKey(
    val authenticationKey: String,
) : EventKey {

    override fun makeKey(): String = "authentication-key::$authenticationKey"

}
