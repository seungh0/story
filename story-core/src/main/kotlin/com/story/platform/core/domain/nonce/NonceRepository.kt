package com.story.platform.core.domain.nonce

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class NonceRepository(
    private val stringRedisRepository: StringRedisRepository<NonceKey, Long>,
) {

    suspend fun validate(nonce: String): Boolean {
        val value = stringRedisRepository.incr(key = NonceKey(token = nonce))
        return value == 0L
    }

    suspend fun generate(nonce: String): Boolean {
        return stringRedisRepository.setIfAbsent(key = NonceKey(token = nonce), value = VALUE)
    }

    companion object {
        private const val VALUE = -1L // -1로 만든다
    }

}
