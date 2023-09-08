package com.story.platform.core.common.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.story.platform.core.common.error.ErrorCode

data class ApiResponse<T>(
    val ok: Boolean,
    val error: String? = null,
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    val messages: List<String>? = null,
    val result: T?,
) {

    companion object {
        fun <T> ok(result: T): ApiResponse<T> = ApiResponse(ok = true, result = result)

        fun <T> fail(
            error: ErrorCode,
            messages: Collection<String>? = null,
        ): ApiResponse<T> = ApiResponse(
            ok = false,
            error = error.code,
            messages = messages?.toList(),
            result = null,
        )

        val OK: ApiResponse<Nothing?> = ok(result = null)
    }

}
