package com.story.platform.core.common.error

enum class ErrorCode(
    val httpStatusCode: Int,
    val code: String,
    val description: String = "", // TOOD: 에러 설명 작성
) {

    /**
     * 400 BadRequest
     */
    E400_INVALID_ARGUMENTS(httpStatusCode = 400, code = "invalid_arguments"),
    E400_INVALID_CURSOR(httpStatusCode = 400, code = "invalid_cursor"),
    E400_INVALID_POST_ID(httpStatusCode = 400, code = "invalid_post_id"),

    /**
     * 401 UnAuthorized
     */
    E401_UNAUTHORIZED(httpStatusCode = 401, code = "invalid_auth"),

    /**
     * 403 Forbidden
     */
    E403_NO_PERMISSION(httpStatusCode = 403, code = "no_permission"),

    /**
     * 404 NotFound
     */
    E404_NOT_EXISTS_AUTHENTICATION_KEY(httpStatusCode = 404, code = "not_exists_authentication_key"),
    E404_NOT_EXISTS_COMPONENT(httpStatusCode = 404, code = "not_exists_component"),
    E404_NOT_EXISTS_RESOURCE(httpStatusCode = 404, code = "not_exists_resource"),
    E404_NOT_EXISTS_POST(httpStatusCode = 404, code = "not_exists_post"),
    E404_NOT_EXISTS_CONNECT_FEED_MAPPING(httpStatusCode = 404, code = "not_exists_connect_mapping_feed"),

    /**
     * 405 Method Not Allowed
     */
    E405_METHOD_NOT_ALLOWED(httpStatusCode = 405, code = "method_not_allowed"),

    /**
     * 409 Conflict
     */
    E409_ALREADY_EXISTS_AUTHENTICATION_KEY(httpStatusCode = 409, code = "already_exists_authentication_key"),
    E409_ALREADY_EXISTS_COMPONENT(httpStatusCode = 409, code = "already_exists_component"),
    E409_ALREADY_CONNECT_FEED_MAPPING(httpStatusCode = 409, code = "already_connect_mapping_feed"),

    E413_PAYLOAD_TOO_LARGE(httpStatusCode = 413, code = "payload_too_large"),
    E413_FILE_SIZE_TOO_LARGE(httpStatusCode = 413, code = "file_size_is"),

    /**
     * 500 InternalServerError
     */
    E500_INTERNAL_ERROR(
        httpStatusCode = 500,
        code = "internal_error",
    ),

    /**
     * 501 NotImplemented
     */
    E501_NOT_SUPPORTED(httpStatusCode = 501, code = "not_supported"),

    /**
     * 503 Service UnAvailable
     */
    E503_SERVICE_UNAVAILABLE(
        httpStatusCode = 503,
        code = "service_unavailable",
    ),

}
