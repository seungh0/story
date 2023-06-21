package com.story.platform.core.common.error

enum class ErrorCode(
    val httpStatusCode: Int,
    val minorStatusCode: String,
    val errorMessage: String,
) {

    /**
     * 400 BadRequest
     */
    E400_BAD_REQUEST(httpStatusCode = 400, minorStatusCode = "000", errorMessage = "Bad Request"),

    /**
     * 401 UnAuthorized
     */
    E401_UNAUTHORIZED(httpStatusCode = 401, minorStatusCode = "000", errorMessage = "UnAuthorized Request"),

    /**
     * 403 Forbidden
     */
    E403_FORBIDDEN(httpStatusCode = 403, minorStatusCode = "000", errorMessage = "Forbidden"),

    /**
     * 404 NotFound
     */
    E404_NOT_FOUND(httpStatusCode = 404, minorStatusCode = "000", errorMessage = "Not Found"),
    E404_NOT_FOUND_WORKSPACE(httpStatusCode = 404, minorStatusCode = "001", errorMessage = "Not Found Workspace"),
    E404_NOT_FOUND_AUTHENTICATION_KEY(
        httpStatusCode = 404,
        minorStatusCode = "002",
        errorMessage = "Not Found Authentication Key"
    ),
    E404_NOT_FOUND_COMPONENT(httpStatusCode = 404, minorStatusCode = "003", errorMessage = "Not Found Component"),
    E404_NOT_FOUND_RESOURCE(httpStatusCode = 404, minorStatusCode = "004", errorMessage = "Not Found Resource"),
    E404_NOT_FOUND_POST(httpStatusCode = 404, minorStatusCode = "005", errorMessage = "Not Found Post"),

    /**
     * 405 Method Not Allowed
     */
    E405_METHOD_NOT_ALLOWED(httpStatusCode = 405, minorStatusCode = "000", errorMessage = "Method Not Allowed"),

    /**
     * 409 Conflict
     */
    E409_CONFLICT(httpStatusCode = 409, minorStatusCode = "000", errorMessage = "Conflict"),
    E409_CONFLICT_AUTHENTICATION_KEY(
        httpStatusCode = 409,
        minorStatusCode = "001",
        errorMessage = "Conflict Authentication Key"
    ),
    E409_CONFLICT_COMPONENT(httpStatusCode = 409, minorStatusCode = "003", errorMessage = "Conflict Component"),

    E413_PAYLOAD_TOO_LARGE(httpStatusCode = 413, minorStatusCode = "000", errorMessage = "Payload is too Large"),
    E413_FILE_SIZE_TOO_LARGE(httpStatusCode = 413, minorStatusCode = "001", errorMessage = "File Size is Too Large"),

    /**
     * 500 InternalServerError
     */
    E500_INTERNAL_SERVER_ERROR(httpStatusCode = 500, minorStatusCode = "000", errorMessage = "Internal Server Error"),

    /**
     * 501 NotImplemented
     */
    E501_NOT_IMPLEMENTED(httpStatusCode = 501, minorStatusCode = "000", errorMessage = "Not Implemented"),

    /**
     * 503 Service UnAvailable
     */
    E503_SERVICE_UNAVAILABLE(httpStatusCode = 503, minorStatusCode = "000", errorMessage = "Service Unavailable"),

}
