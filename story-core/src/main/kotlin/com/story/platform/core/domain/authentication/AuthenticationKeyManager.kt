package com.story.platform.core.domain.authentication

import com.story.platform.core.common.error.ConflictException
import com.story.platform.core.common.error.ErrorCode
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
        authenticationKey: String,
        description: String,
    ) {
        if (isAlreadyRegisterKey(workspaceId = workspaceId, authenticationKey = authenticationKey)) {
            throw ConflictException(
                message = "워크스페이스($workspaceId)에 이미 등록된 인증 키($authenticationKey)입니다",
                errorCode = ErrorCode.E409_CONFLICT_AUTHENTICATION_KEY,
            )
        }

        val authenticationKey = AuthenticationKey.of(
            workspaceId = workspaceId,
            apiKey = authenticationKey,
            description = description,
        )

        reactiveCassandraOperations.batchOps()
            .insert(authenticationKey)
            .insert(AuthenticationReverseKey.from(authenticationKey))
            .executeCoroutine()
    }

    private suspend fun isAlreadyRegisterKey(
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

    @CacheEvict(
        cacheType = CacheType.AUTHENTICATION_REVERSE_KEY,
        key = "'workspaceId:' + {#workspaceId} + ':authenticationKey:' + {#authenticationKey}",
        condition = "#status != null"
    )
    suspend fun modify(
        workspaceId: String,
        authenticationKey: String,
        description: String?,
        status: AuthenticationKeyStatus?,
    ) {
        val authenticationKey = findAuthenticationKey(workspaceId = workspaceId, authenticationKey = authenticationKey)

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

    private suspend fun findAuthenticationKey(workspaceId: String, authenticationKey: String): AuthenticationKey {
        return authenticationKeyRepository.findById(
            AuthenticationKeyPrimaryKey(
                workspaceId = workspaceId,
                authenticationKey = authenticationKey,
            )
        ) ?: throw NotFoundException(
            message = "워크스페이스($workspaceId)에 등록되지 않은 인증 키($authenticationKey) 입니다",
            errorCode = ErrorCode.E404_NOT_FOUND_AUTHENTICATION_KEY,
        )
    }

}
