package com.story.platform.core.domain.authentication

import com.story.platform.core.common.enums.ServiceType
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
        serviceType: ServiceType,
        apiKey: String,
        description: String,
    ) {
        if (isAlreadyRegisterKey(serviceType = serviceType, apiKey = apiKey)) {
            throw ConflictException("이미 등록된 서비스($serviceType) API-Key($apiKey)입니다")
        }

        val authenticationKey = AuthenticationKey.of(
            serviceType = serviceType,
            apiKey = apiKey,
            description = description,
        )

        reactiveCassandraOperations.batchOps()
            .insert(authenticationKey)
            .insert(AuthenticationReverseKey.from(authenticationKey))
            .executeCoroutine()
    }

    private suspend fun isAlreadyRegisterKey(
        serviceType: ServiceType,
        apiKey: String,
    ): Boolean {
        return authenticationKeyRepository.existsById(
            AuthenticationKeyPrimaryKey(
                serviceType = serviceType,
                apiKey = apiKey,
            )
        )
    }

    @CacheEvict(
        cacheType = CacheType.AUTHENTICATION_REVERSE_KEY,
        key = "'serviceType:' + {#serviceType} + ':apiKey:' + {#apiKey}",
        condition = "#status != null"
    )
    suspend fun modify(
        serviceType: ServiceType,
        apiKey: String,
        description: String?,
        status: AuthenticationKeyStatus?,
    ) {
        val authenticationKey = findAuthenticationKey(serviceType = serviceType, apiKey = apiKey)

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

    private suspend fun findAuthenticationKey(serviceType: ServiceType, apiKey: String): AuthenticationKey {
        return authenticationKeyRepository.findById(
            AuthenticationKeyPrimaryKey(
                serviceType = serviceType,
                apiKey = apiKey,
            )
        ) ?: throw NotFoundException("서비스($serviceType)에 등록되지 않은 API-Key($apiKey) 입니다")
    }

}
