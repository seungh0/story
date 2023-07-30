package com.story.platform.core.domain.authentication

import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class AuthenticationKeyCreator(
    private val authenticationKeyRepository: AuthenticationKeyRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun createAuthenticationKey(
        workspaceId: String,
        authenticationKey: String,
        description: String,
    ) {
        if (existsAuthenticationKey(workspaceId = workspaceId, authenticationKey = authenticationKey)) {
            throw AuthenticationKeyAlreadyExistsException(message = "워크스페이스($workspaceId)에 이미 등록된 인증 키($authenticationKey)입니다")
        }

        val authentication = AuthenticationKey.of(
            workspaceId = workspaceId,
            authenticationKey = authenticationKey,
            description = description,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(authentication)
            .upsert(AuthenticationReverseKey.from(authentication))
            .executeCoroutine()
    }

    private suspend fun existsAuthenticationKey(
        workspaceId: String,
        authenticationKey: String,
    ): Boolean {
        return authenticationKeyRepository.existsById(
            AuthenticationKeyPrimaryKey(
                workspaceId = workspaceId,
                authenticationKey = authenticationKey,
            )
        )
    }

}
