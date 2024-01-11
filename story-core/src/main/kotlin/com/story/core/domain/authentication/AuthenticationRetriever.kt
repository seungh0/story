package com.story.core.domain.authentication

import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cache.Cacheable
import org.springframework.stereotype.Service

@Service
class AuthenticationRetriever(
    private val authenticationRepository: AuthenticationRepository,
) {

    @Cacheable(
        cacheType = CacheType.AUTHENTICATION_REVERSE_KEY,
        key = "'authenticationKey:' + {#authenticationKey}",
    )
    suspend fun getAuthentication(
        authenticationKey: String,
    ): AuthenticationResponse {
        return AuthenticationResponse.of(
            authenticationRepository.findById(
                AuthenticationPrimaryKey(authenticationKey = authenticationKey)
            ) ?: throw AuthenticationKeyNotExistsException(message = "등록되지 않은 인증 키($authenticationKey) 입니다")
        )
    }

}
