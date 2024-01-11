package com.story.core.domain.authentication

import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class AuthenticationCreator(
    private val workspaceAuthenticationRepository: WorkspaceAuthenticationRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun createAuthentication(
        workspaceId: String,
        authenticationKey: String,
        description: String,
    ) {
        if (existsAuthenticationKey(workspaceId = workspaceId, authenticationKey = authenticationKey)) {
            throw AuthenticationKeyAlreadyExistsException(message = "워크스페이스($workspaceId)에 이미 등록된 인증 키($authenticationKey)입니다")
        }

        val authentication = WorkspaceAuthentication.of(
            workspaceId = workspaceId,
            authenticationKey = authenticationKey,
            description = description,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(authentication)
            .upsert(Authentication.from(authentication))
            .executeCoroutine()
    }

    private suspend fun existsAuthenticationKey(
        workspaceId: String,
        authenticationKey: String,
    ): Boolean {
        return workspaceAuthenticationRepository.existsById(
            WorkspaceAuthenticationPrimaryKey(
                workspaceId = workspaceId,
                authenticationKey = authenticationKey,
            )
        )
    }

}
