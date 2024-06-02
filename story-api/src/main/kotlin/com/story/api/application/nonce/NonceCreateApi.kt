package com.story.api.application.nonce

import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.nonce.NonceManager
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class NonceCreateApi(
    private val nonceManager: NonceManager,
) {

    @PostMapping("/v1/nonce")
    suspend fun createNonce(
        @Valid @RequestBody request: NonceCreateRequest,
    ): ApiResponse<NonceResponse> {
        val nonce = nonceManager.create(request.expirationSeconds)
        return ApiResponse.ok(NonceResponse(nonce = nonce))
    }

}
