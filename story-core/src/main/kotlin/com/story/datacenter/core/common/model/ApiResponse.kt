package com.story.datacenter.core.common.model

import com.story.datacenter.core.common.error.ErrorCode

data class ApiResponse<T>(
    val code: String = "",
    val message: String = "",
    val result: T?,
) {

    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(result = data)

        fun fail(
            error: ErrorCode,
            message: String? = null,
        ): ApiResponse<Nothing> = ApiResponse(
            code = error.minorStatusCode,
            message = message ?: error.errorMessage,
            result = null,
        )

        val OK = success(data = "OK")
    }

}
