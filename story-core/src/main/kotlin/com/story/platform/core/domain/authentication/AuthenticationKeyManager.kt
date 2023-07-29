package com.story.platform.core.domain.authentication

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaTopicFinder
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.support.cache.CacheEvict
import com.story.platform.core.support.cache.CacheStrategyType
import com.story.platform.core.support.cache.CacheType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class AuthenticationKeyManager(
    private val authenticationKeyRepository: AuthenticationKeyRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,

    @IOBound
    private val dispatcher: CoroutineDispatcher,

    @Qualifier(KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    suspend fun createAuthenticationKey(
        workspaceId: String,
        authenticationKey: String,
        description: String,
    ) {
        if (isAlreadyRegisterKey(workspaceId = workspaceId, authenticationKey = authenticationKey)) {
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
        key = "'authenticationKey:' + {#authenticationKey}",
        targetCacheStrategies = [CacheStrategyType.GLOBAL],
    )
    suspend fun patchAuthenticationKey(
        workspaceId: String,
        authenticationKey: String,
        description: String?,
        status: AuthenticationKeyStatus?,
    ) {
        val authentication = findAuthentication(workspaceId = workspaceId, authenticationKey = authenticationKey)

        authentication.patch(
            description = description,
            status = status,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(authentication)
            .upsert(AuthenticationReverseKey.from(authentication))
            .executeCoroutine()

        withContext(dispatcher) {
            kafkaTemplate.send(
                KafkaTopicFinder.getTopicName(TopicType.AUTHENTICATION_KEY),
                AuthenticationKeyEvent.updated(authenticationKey = authentication).toJson()
            )
        }
    }

    private suspend fun findAuthentication(workspaceId: String, authenticationKey: String): AuthenticationKey {
        return authenticationKeyRepository.findById(
            AuthenticationKeyPrimaryKey(
                workspaceId = workspaceId,
                authenticationKey = authenticationKey,
            )
        )
            ?: throw AuthenticationKeyNotExistsException(message = "워크스페이스($workspaceId)에 등록되지 않은 인증 키($authenticationKey) 입니다")
    }

}
