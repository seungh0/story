package com.story.platform.api.domain.token

import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.nonce.NonceManager
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NonceCreateApi(
    private val nonceManager: NonceManager,
) {

    @PostMapping("/v1/nonce")
    suspend fun create(): ApiResponse<NonceApiResponse> {
        val nonce = nonceManager.generate()
        return ApiResponse.ok(NonceApiResponse(nonce = nonce))
    }

}
