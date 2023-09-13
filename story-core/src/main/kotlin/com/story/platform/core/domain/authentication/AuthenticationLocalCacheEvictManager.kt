package com.story.platform.core.domain.authentication

import com.story.platform.core.common.logger.LoggerExtension.log
import com.story.platform.core.infrastructure.cache.CacheEvict
import com.story.platform.core.infrastructure.cache.CacheStrategy
import com.story.platform.core.infrastructure.cache.CacheType
import org.springframework.stereotype.Service

@Service
class AuthenticationLocalCacheEvictManager {

    @CacheEvict(
        cacheType = CacheType.AUTHENTICATION_REVERSE_KEY,
        key = "'authenticationKey:' + {#authenticationKey}",
        targetCacheStrategies = [CacheStrategy.LOCAL],
    )
    suspend fun evictAuthenticationKey(
        authenticationKey: String,
    ) {
        log.info { "Authentication 캐시가 만료됩니다 [authenticationKey: $authenticationKey]" }
    }

}
