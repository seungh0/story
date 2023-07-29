package com.story.platform.core.domain.authentication

import com.story.platform.core.common.logger.LoggerExtension.log
import com.story.platform.core.support.cache.CacheEvict
import com.story.platform.core.support.cache.CacheStrategyType
import com.story.platform.core.support.cache.CacheType
import org.springframework.stereotype.Service

@Service
class AuthenticationKeyLocalCacheEvictionManager {

    @CacheEvict(
        cacheType = CacheType.AUTHENTICATION_REVERSE_KEY,
        key = "'authenticationKey:' + {#authenticationKey}",
        targetCacheStrategies = [CacheStrategyType.LOCAL],
    )
    suspend fun evictAuthenticationKey(
        authenticationKey: String,
    ) {
        log.info { "AuthenticationKey 캐시가 만료됩니다 [authenticationKey: $authenticationKey]" }
    }

}
