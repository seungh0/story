package com.story.core.domain.nonce

interface NonceRepository {

    suspend fun validate(nonce: String): Boolean

    suspend fun generate(nonce: String, expirationSeconds: Long): Boolean

}
