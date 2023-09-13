package com.story.platform.core.domain.authentication

import com.story.platform.core.infrastructure.cache.CacheEvict
import com.story.platform.core.infrastructure.cache.CacheStrategy
import com.story.platform.core.infrastructure.cache.CacheType
import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class AuthenticationModifier(
    private val workspaceAuthenticationRepository: WorkspaceAuthenticationRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val authenticationEventProducer: AuthenticationEventProducer,
) {

    @CacheEvict(
        cacheType = CacheType.AUTHENTICATION_REVERSE_KEY,
        key = "'authenticationKey:' + {#authenticationKey}",
        targetCacheStrategies = [CacheStrategy.GLOBAL],
    )
    suspend fun patchAuthenticationKey(
        workspaceId: String,
        authenticationKey: String,
        description: String?,
        status: AuthenticationStatus?,
    ) {
        val authentication = findAuthentication(workspaceId = workspaceId, authenticationKey = authenticationKey)

        authentication.patch(
            description = description,
            status = status,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(authentication)
            .upsert(Authentication.from(authentication))
            .executeCoroutine()

        authenticationEventProducer.publishEvent(
            authenticationKey = authenticationKey,
            event = AuthenticationEvent.updated(workspaceAuthentication = authentication),
        )
    }

    private suspend fun findAuthentication(workspaceId: String, authenticationKey: String): WorkspaceAuthentication {
        return workspaceAuthenticationRepository.findById(
            WorkspaceAuthenticationPrimaryKey(
                workspaceId = workspaceId,
                authenticationKey = authenticationKey,
            )
        )
            ?: throw AuthenticationKeyNotExistsException(message = "워크스페이스($workspaceId)에 등록되지 않은 인증 키($authenticationKey) 입니다")
    }

}
