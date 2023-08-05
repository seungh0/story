package com.story.platform.core.domain.authentication

import com.story.platform.core.support.cache.CacheType
import com.story.platform.core.support.cache.Cacheable
import org.springframework.stereotype.Service

@Service
class AuthenticationKeyRetriever(
    private val authenticationKeyRepository: AuthenticationKeyRepository,
) {

    @Cacheable(
        cacheType = CacheType.AUTHENTICATION_REVERSE_KEY,
        key = "'authenticationKey:' + {#authenticationKey}",
    )
    suspend fun getAuthenticationKey(
        authenticationKey: String,
    ): AuthenticationKeyResponse {
        return AuthenticationKeyResponse.of(
            authenticationKeyRepository.findById(
                AuthenticationKeyPrimaryKey(authenticationKey = authenticationKey)
            ) ?: throw AuthenticationKeyNotExistsException(message = "등록되지 않은 인증 키($authenticationKey) 입니다")
        )
    }

}
