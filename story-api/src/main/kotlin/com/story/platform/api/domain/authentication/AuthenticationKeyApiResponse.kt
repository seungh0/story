package com.story.platform.api.domain.authentication

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
import com.story.platform.core.domain.authentication.AuthenticationResponse

data class AuthenticationKeyApiResponse(
    val serviceType: ServiceType,
    val apiKey: String,
    val status: AuthenticationKeyStatus,
) {

    companion object {
        fun of(
            authenticationKey: AuthenticationResponse,
        ) = AuthenticationKeyApiResponse(
            serviceType = authenticationKey.serviceType,
            apiKey = authenticationKey.apiKey,
            status = authenticationKey.status,
        )
    }

}
