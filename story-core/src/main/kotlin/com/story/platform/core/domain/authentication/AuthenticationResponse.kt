package com.story.platform.core.domain.authentication

import com.story.platform.core.common.enums.ServiceType

data class AuthenticationResponse(
    val serviceType: ServiceType,
    val apiKey: String,
    val status: AuthenticationKeyStatus,
) {

    fun isActivated() = this.status.isActivated

    companion object {
        fun of(authenticationReverseKey: AuthenticationReverseKey) = AuthenticationResponse(
            serviceType = authenticationReverseKey.serviceType,
            apiKey = authenticationReverseKey.key.apiKey,
            status = authenticationReverseKey.status,
        )
    }

}
