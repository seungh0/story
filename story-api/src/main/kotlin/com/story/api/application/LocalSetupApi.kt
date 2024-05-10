package com.story.api.application

import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.apikey.ApiKeyEntity
import com.story.core.domain.apikey.WorkspaceApiKey
import com.story.core.domain.workspace.WorkspaceEntity
import com.story.core.domain.workspace.WorkspacePricePlan
import com.story.core.infrastructure.cassandra.upsert
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.context.annotation.Profile
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Profile("local")
@RestController
class LocalSetupApi(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    @PostMapping("/setup")
    suspend fun setup(
        @RequestParam workspaceId: String,
        @RequestParam apiKey: String,
    ): ApiResponse<Nothing?> {
        reactiveCassandraOperations.batchOps()
            .upsert(
                ApiKeyEntity.of(
                    apiKey = apiKey,
                    workspaceId = workspaceId,
                    description = "story",
                )
            )
            .upsert(
                WorkspaceEntity.of(
                    workspaceId = workspaceId,
                    name = "story",
                    plan = WorkspacePricePlan.FREE,
                )
            )
            .upsert(
                WorkspaceApiKey.of(
                    workspaceId = workspaceId,
                    apiKey = apiKey,
                    description = "story"
                )
            )
            .execute()
            .awaitSingle()

        return ApiResponse.OK
    }

}
