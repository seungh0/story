package com.story.platform.core.domain.authentication

import com.story.platform.core.common.error.NotFoundException
import com.story.platform.core.support.cache.CacheType
import com.story.platform.core.support.cache.Cacheable
import org.springframework.stereotype.Service

@Service
class AuthenticationKeyRetriever(
    private val authenticationReverseKeyRepository: AuthenticationReverseKeyRepository,
) {

    @Cacheable(
        cacheType = CacheType.AUTHENTICATION_REVERSE_KEY,
        key = "'apiKey:' + {#apiKey}",
    )
    suspend fun getAuthenticationKey(
        apiKey: String,
    ): AuthenticationReverseKey {
        return authenticationReverseKeyRepository.findById(
            AuthenticationReverseKeyPrimaryKey(
                apiKey = apiKey,
            )
        ) ?: throw NotFoundException("등록되지 않은 API-Key($apiKey) 입니다")
    }

}
