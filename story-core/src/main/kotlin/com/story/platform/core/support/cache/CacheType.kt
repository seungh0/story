package com.story.platform.core.support.cache

import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.component.ComponentResponse
import java.time.Duration

enum class CacheType(
    private val description: String,
    val key: String,
    val localCacheTtl: Duration? = null,
    val globalCacheTtl: Duration? = null,
    val cacheClazz: Class<out Any?>,
) {

    AUTHENTICATION_REVERSE_KEY(
        description = "인증 키 정보",
        key = "authentication-key:v1",
        globalCacheTtl = Duration.ofHours(1),
        localCacheTtl = Duration.ofSeconds(10),
        cacheClazz = AuthenticationResponse::class.java,
    ),
    COMPONENT(
        description = "컴포넌트 정보",
        key = "component:v1",
        globalCacheTtl = Duration.ofHours(1),
        localCacheTtl = Duration.ofSeconds(10),
        cacheClazz = ComponentResponse::class.java,
    ),
    ;

    fun enableLocalCache(): Boolean {
        return localCacheTtl != null && Duration.ZERO < localCacheTtl
    }

    fun enableGlobalCache(): Boolean {
        return globalCacheTtl != null && Duration.ZERO < globalCacheTtl
    }

    companion object {
        @JvmField
        val LOCAL_CACHE_TYPES = CacheType.values()
            .filter { cacheType -> cacheType.enableLocalCache() }
    }

}
