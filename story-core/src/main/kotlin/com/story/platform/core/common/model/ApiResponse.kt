package com.story.platform.core.common.model

import com.fasterxml.jackson.annotation.JsonInclude

data class ApiResponse<T>(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val code: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val message: String? = null,

    val result: T?,
) {

    companion object {
        fun <T> success(result: T): com.story.platform.core.common.model.ApiResponse<T> =
            com.story.platform.core.common.model.ApiResponse(result = result)

        fun fail(
            error: com.story.platform.core.common.error.ErrorCode,
            message: String? = null,
        ): com.story.platform.core.common.model.ApiResponse<Nothing> = com.story.platform.core.common.model.ApiResponse(
            code = "${error.httpStatusCode}${error.minorStatusCode}",
            message = message ?: error.errorMessage,
            result = null,
        )

        val OK = com.story.platform.core.common.model.ApiResponse.Companion.success(result = "OK")
    }

}
