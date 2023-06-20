package com.story.platform.core.domain.authentication

import com.story.platform.core.common.error.ConflictException
import com.story.platform.core.common.error.NotFoundException
import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.support.cache.CacheEvict
import com.story.platform.core.support.cache.CacheType
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class AuthenticationKeyManager(
    private val authenticationKeyRepository: AuthenticationKeyRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun register(
        workspaceId: String,
        apiKey: String,
        description: String,
    ) {
        if (isAlreadyRegisterKey(workspaceId = workspaceId, apiKey = apiKey)) {
            throw ConflictException("이미 등록된 워크스페이스($workspaceId)의 API-Key($apiKey)입니다")
        }

        val authenticationKey = AuthenticationKey.of(
            workspaceId = workspaceId,
            apiKey = apiKey,
            description = description,
        )

        reactiveCassandraOperations.batchOps()
            .insert(authenticationKey)
            .insert(AuthenticationReverseKey.from(authenticationKey))
            .executeCoroutine()
    }

    private suspend fun isAlreadyRegisterKey(
        workspaceId: String,
        apiKey: String,
    ): Boolean {
        return authenticationKeyRepository.existsById(
            AuthenticationKeyPrimaryKey(
                workspaceId = workspaceId,
                apiKey = apiKey,
            )
        )
    }

    @CacheEvict(
        cacheType = CacheType.AUTHENTICATION_REVERSE_KEY,
        key = "'workspaceId:' + {#workspaceId} + ':apiKey:' + {#apiKey}",
        condition = "#status != null"
    )
    suspend fun modify(
        workspaceId: String,
        apiKey: String,
        description: String?,
        status: AuthenticationKeyStatus?,
    ) {
        val authenticationKey = findAuthenticationKey(workspaceId = workspaceId, apiKey = apiKey)

        val hasChanged = authenticationKey.patch(
            description = description,
            status = status,
        )

        if (!hasChanged) {
            return
        }

        reactiveCassandraOperations.batchOps()
            .insert(authenticationKey)
            .insert(AuthenticationReverseKey.from(authenticationKey))
            .executeCoroutine()
    }

    private suspend fun findAuthenticationKey(workspaceId: String, apiKey: String): AuthenticationKey {
        return authenticationKeyRepository.findById(
            AuthenticationKeyPrimaryKey(
                workspaceId = workspaceId,
                apiKey = apiKey,
            )
        ) ?: throw NotFoundException("워크스페이스($workspaceId)에 등록되지 않은 API-Key($apiKey) 입니다")
    }

}
