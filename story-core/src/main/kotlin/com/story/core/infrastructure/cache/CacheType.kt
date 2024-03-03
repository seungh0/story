package com.story.core.infrastructure.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.story.core.domain.apikey.ApiKeyResponse
import com.story.core.domain.component.ComponentResponse
import com.story.core.domain.feed.mapping.FeedMappingResponse
import com.story.core.domain.post.PostResponse
import com.story.core.domain.workspace.WorkspaceResponse
import java.time.Duration
import java.util.Optional

enum class CacheType(
    private val description: String,
    val key: String,
    val localCacheTtl: Duration? = null,
    val globalCacheTtl: Duration? = null,
    val typeReference: TypeReference<*>,
) {

    API_KEY_REVERSE(
        description = "API 키 정보",
        key = "api-key:v1",
        localCacheTtl = Duration.ofMinutes(3),
        globalCacheTtl = Duration.ofMinutes(30),
        typeReference = object : TypeReference<Optional<ApiKeyResponse>>() {}
    ),
    COMPONENT(
        description = "컴포넌트 정보",
        key = "component:v1",
        localCacheTtl = Duration.ofMinutes(3),
        globalCacheTtl = Duration.ofMinutes(30),
        typeReference = object : TypeReference<Optional<ComponentResponse>>() {}
    ),
    WORKSPACE(
        description = "워크스페이스",
        key = "workspace:v1",
        localCacheTtl = Duration.ofMinutes(3),
        globalCacheTtl = Duration.ofMinutes(30),
        typeReference = object : TypeReference<Optional<WorkspaceResponse>>() {}
    ),
    FEED_MAPPING(
        description = "피드 매핑 목록",
        key = "feed-mapping:v1",
        localCacheTtl = Duration.ofMinutes(3),
        globalCacheTtl = Duration.ofMinutes(30),
        typeReference = object : TypeReference<List<FeedMappingResponse>>() {}
    ),
    POST(
        description = "포스트",
        key = "post:v1",
        localCacheTtl = Duration.ofMinutes(1),
        globalCacheTtl = Duration.ofMinutes(5),
        typeReference = object : TypeReference<PostResponse>() {}
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
        val LOCAL_CACHE_TYPES = entries
            .filter { cacheType -> cacheType.enableLocalCache() }
    }

}
