package com.story.platform.core.domain.authentication

import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import com.story.platform.core.support.cache.CacheEvict
import com.story.platform.core.support.cache.CacheStrategyType
import com.story.platform.core.support.cache.CacheType
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class AuthenticationModifier(
    private val workspaceAuthenticationRepository: WorkspaceAuthenticationRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val authentication1EventPublisher: Authentication1EventPublisher,
) {

    @CacheEvict(
        cacheType = CacheType.AUTHENTICATION_REVERSE_KEY,
        key = "'authenticationKey:' + {#authenticationKey}",
        targetCacheStrategies = [CacheStrategyType.GLOBAL],
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

        authentication1EventPublisher.publishEvent(
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