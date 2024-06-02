package com.story.core.common.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.story.core.common.error.ErrorCode

data class ApiResponse<T>(
    val ok: Boolean,
    val error: String? = null,
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    val reasons: List<String>? = null,
    val result: T?,
) {

    companion object {
        fun <T> ok(result: T): ApiResponse<T> = ApiResponse(ok = true, result = result)

        fun <T> fail(
            error: ErrorCode,
            reasons: Collection<String>? = null,
        ): ApiResponse<T> = ApiResponse(
            ok = false,
            error = error.code,
            reasons = reasons?.toList(),
            result = null,
        )

        val OK: ApiResponse<Nothing?> = ok(result = null)
    }

}
