package com.story.core.infrastructure.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.story.core.domain.apikey.ApiKey
import com.story.core.domain.component.Component
import com.story.core.domain.feed.mapping.FeedMapping
import com.story.core.domain.post.Post
import com.story.core.domain.workspace.Workspace
import java.time.Duration
import java.util.Optional

enum class CacheType(
    private val description: String,
    val key: String,
    val localCacheTtl: Duration? = null,
    val globalCacheTtl: Duration? = null,
    val typeReference: TypeReference<*>,
) {

    API_KEY(
        description = "API 키 정보",
        key = "api-key:v1",
        localCacheTtl = Duration.ofMinutes(3),
        globalCacheTtl = Duration.ofMinutes(30),
        typeReference = object : TypeReference<Optional<ApiKey>>() {}
    ),
    COMPONENT(
        description = "컴포넌트 정보",
        key = "component:v1",
        localCacheTtl = Duration.ofMinutes(3),
        globalCacheTtl = Duration.ofMinutes(30),
        typeReference = object : TypeReference<Optional<Component>>() {}
    ),
    WORKSPACE(
        description = "워크스페이스",
        key = "workspace:v1",
        localCacheTtl = Duration.ofMinutes(3),
        globalCacheTtl = Duration.ofMinutes(30),
        typeReference = object : TypeReference<Optional<Workspace>>() {}
    ),
    FEED_MAPPING(
        description = "피드 매핑 목록",
        key = "feed-mapping:v1",
        localCacheTtl = Duration.ofMinutes(3),
        globalCacheTtl = Duration.ofMinutes(30),
        typeReference = object : TypeReference<List<FeedMapping>>() {}
    ),
    POST(
        description = "포스트",
        key = "post:v1",
        localCacheTtl = Duration.ofMinutes(1),
        globalCacheTtl = Duration.ofMinutes(5),
        typeReference = object : TypeReference<Post>() {}
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
