package com.story.api.application

import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.apikey.ApiKeyWriteRepository
import com.story.core.domain.workspace.WorkspacePricePlan
import com.story.core.domain.workspace.WorkspaceWriteRepository
import com.story.core.support.cache.CacheManager
import com.story.core.support.cache.CacheStrategy
import com.story.core.support.cache.CacheType
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Profile("local")
@RestController
class LocalSetupApi(
    private val apiKeyWriteRepository: ApiKeyWriteRepository,
    private val workspaceWriteRepository: WorkspaceWriteRepository,
    private val cacheManager: CacheManager,
) {

    @PostMapping("/setup")
    suspend fun setup(
        @RequestParam workspaceId: String,
        @RequestParam apiKey: String,
    ): ApiResponse<Nothing?> {
        apiKeyWriteRepository.create(
            key = apiKey,
            workspaceId = workspaceId,
            description = "story",
        )
        workspaceWriteRepository.create(
            workspaceId = workspaceId,
            name = "story",
            plan = WorkspacePricePlan.FREE,
        )

        return ApiResponse.OK
    }

    @DeleteMapping("/test/caches-refresh")
    suspend fun refreshCaches(): ApiResponse<Nothing?> {
        CacheType.entries.forEach { cacheType ->
            cacheManager.evictAll(cacheStrategy = CacheStrategy.LOCAL, cacheType = cacheType)
            cacheManager.evictAll(cacheStrategy = CacheStrategy.GLOBAL, cacheType = cacheType)
        }
        return ApiResponse.OK
    }

}
