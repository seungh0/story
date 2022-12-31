package com.story.pushcenter.apiconsumer.common.dto.response

data class ApiResponse<T>(
    val code: String = "",
    val message: String = "",
    val data: T?,
) {

    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(data = data)

        val OK = success(data = "OK")
    }

}
