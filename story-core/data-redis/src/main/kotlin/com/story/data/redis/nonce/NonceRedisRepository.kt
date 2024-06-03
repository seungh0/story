package com.story.data.redis.nonce

import com.story.core.domain.nonce.NonceRepository
import com.story.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class NonceRedisRepository(
    private val stringRedisRepository: StringRedisRepository<NonceKey, Long>,
) : NonceRepository {

    override suspend fun validate(nonce: String): Boolean {
        return stringRedisRepository.del(key = NonceKey(nonce = nonce))
    }

    override suspend fun generate(nonce: String, expirationSeconds: Long): Boolean {
        return stringRedisRepository.setIfAbsentWithTtl(
            key = NonceKey(nonce = nonce),
            value = VALUE,
            ttl = Duration.ofSeconds(expirationSeconds)
        )
    }

    companion object {
        private const val VALUE = -1L // -1로 만든다
    }

}
