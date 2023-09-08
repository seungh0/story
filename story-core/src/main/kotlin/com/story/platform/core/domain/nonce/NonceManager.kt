package com.story.platform.core.domain.nonce

import org.springframework.stereotype.Service
import java.util.UUID

/**
 * 중복 호출을 방지를 위한 NonceResponse
 * 1. API 호출 전에, Nonce를 할당 받는다.
 * 2. API 호출시, Nonce를 포함해서 요청한다.
 * 3. 유효한 NonceResponse(할당되었고, 처음 사용되는 NonceResponse)인 경우에만 요청을 처리한다
 */
@Service
class NonceManager(
    private val nonceRepository: NonceRepository,
) {

    suspend fun generate(): String {
        var current = 0L
        while (current++ < MAX_COUNT) {
            val nonce = UUID.randomUUID().toString()
            val available = nonceRepository.generate(nonce = nonce)
            if (available) {
                return nonce
            }
        }
        throw NonceGenerateFailedException("NonceResponse 생성에 실패하였습니다. [최대 재시도 횟수=$MAX_COUNT]")
    }

    suspend fun verify(nonce: String) {
        if (!nonceRepository.validate(nonce = nonce)) {
            throw NonceInvalidException("사용할 수 없는 NonceResponse($nonce)입니다")
        }
    }

    companion object {
        private const val MAX_COUNT = 3
    }

}
