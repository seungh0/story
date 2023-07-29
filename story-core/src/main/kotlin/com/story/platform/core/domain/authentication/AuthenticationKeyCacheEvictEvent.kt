package com.story.platform.core.domain.authentication

import com.story.platform.core.support.cache.CacheEvictEventRecord
import com.story.platform.core.support.cache.CacheType

data class AuthenticationKeyCacheEvictEvent(
    val authenticationKey: String,
) {

    companion object {
        fun of(authenticationKey: String) = CacheEvictEventRecord(
            cacheType = CacheType.AUTHENTICATION_REVERSE_KEY,
            payload = AuthenticationKeyCacheEvictEvent(
                authenticationKey = authenticationKey,
            )
        )
    }

}
