package com.story.platform.core.domain.nonce

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class NonceRepository(
    private val stringRedisRepository: StringRedisRepository<NonceKey, Long>,
) {

    suspend fun validate(nonce: String): Boolean {
        val value = stringRedisRepository.incr(key = NonceKey(token = nonce))
        return value == 0L
    }

    suspend fun generate(nonce: String, expirationSeconds: Long): Boolean {
        return stringRedisRepository.setIfAbsentWithTtl(
            key = NonceKey(token = nonce),
            value = VALUE,
            ttl = Duration.ofSeconds(expirationSeconds)
        )
    }

    companion object {
        private const val VALUE = -1L // -1로 만든다
    }

}