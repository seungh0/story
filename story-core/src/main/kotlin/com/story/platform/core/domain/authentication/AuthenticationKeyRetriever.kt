package com.story.platform.core.domain.authentication

import com.story.platform.core.support.cache.CacheType
import com.story.platform.core.support.cache.Cacheable
import org.springframework.stereotype.Service

@Service
class AuthenticationKeyRetriever(
    private val authenticationReverseKeyRepository: AuthenticationReverseKeyRepository,
) {

    @Cacheable(
        cacheType = CacheType.AUTHENTICATION_REVERSE_KEY,
        key = "'authenticationKey:' + {#authenticationKey}",
    )
    suspend fun getAuthenticationKey(
        authenticationKey: String,
    ): AuthenticationResponse {
        return AuthenticationResponse.of(
            authenticationReverseKeyRepository.findById(
                AuthenticationReverseKeyPrimaryKey(
                    authenticationKey = authenticationKey,
                )
            ) ?: throw AuthenticationKeyNotExistsException(message = "등록되지 않은 인증 키($authenticationKey) 입니다")
        )
    }

}
