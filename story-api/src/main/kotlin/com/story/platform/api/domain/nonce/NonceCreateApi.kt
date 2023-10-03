package com.story.platform.api.domain.nonce

import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.nonce.NonceManager
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
        @Valid @RequestBody request: NonceCreateApiRequest,
    ): ApiResponse<NonceApiResponse> {
        val nonce = nonceManager.create(request.expirationSeconds)
        return ApiResponse.ok(NonceApiResponse(nonce = nonce))
    }

}
