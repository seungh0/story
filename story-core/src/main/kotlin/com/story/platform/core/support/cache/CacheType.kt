package com.story.platform.core.support.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.component.ComponentResponse
import com.story.platform.core.domain.feed.mapping.FeedMappingResponse
import com.story.platform.core.domain.workspace.WorkspaceResponse
import java.time.Duration

enum class CacheType(
    private val description: String,
    val key: String,
    val localCacheTtl: Duration? = null,
    val globalCacheTtl: Duration? = null,
    val typeReference: TypeReference<*>,
) {

    AUTHENTICATION_REVERSE_KEY(
        description = "인증 키 정보",
        key = "authentication-key:v1",
        localCacheTtl = Duration.ofMinutes(1),
        globalCacheTtl = Duration.ofHours(1),
        typeReference = object : TypeReference<AuthenticationResponse>() {}
    ),
    COMPONENT(
        description = "컴포넌트 정보",
        key = "component:v1",
        localCacheTtl = Duration.ofMinutes(1),
        globalCacheTtl = Duration.ofHours(1),
        typeReference = object : TypeReference<ComponentResponse>() {}
    ),
    WORKSPACE(
        description = "워크스페이스",
        key = "workspace:v1",
        localCacheTtl = Duration.ofMinutes(1),
        globalCacheTtl = Duration.ofHours(1),
        typeReference = object : TypeReference<WorkspaceResponse>() {}
    ),
    FEED_MAPPING_CONFIGURATIONS(
        description = "피드 매핑 설정 목록",
        key = "feed-mapping:v1",
        localCacheTtl = Duration.ofMinutes(1),
        globalCacheTtl = Duration.ofHours(1),
        typeReference = object : TypeReference<List<FeedMappingResponse>>() {}
    )
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
