package com.story.api.application

import com.story.core.domain.authentication.Authentication
import com.story.core.domain.authentication.WorkspaceAuthentication
import com.story.core.domain.workspace.Workspace
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
        @RequestParam authenticationKey: String,
    ) {
        val authentication = Authentication.of(
            authenticationKey = authenticationKey,
            workspaceId = workspaceId,
            description = "story",
        )
        val workspace = Workspace.of(
            workspaceId = workspaceId,
            name = "story",
            plan = WorkspacePricePlan.FREE,
        )
        val workspaceAuthentication = WorkspaceAuthentication.of(
            workspaceId = workspaceId,
            authenticationKey = authenticationKey,
            description = "story"
        )

        reactiveCassandraOperations.batchOps()
            .upsert(authentication)
            .upsert(workspace)
            .upsert(workspaceAuthentication)
            .execute()
            .awaitSingle()
    }

}
