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

    /**
     * 405 Method Not Allowed
     */
    E405_METHOD_NOT_ALLOWED(httpStatusCode = 405, minorStatusCode = "000", errorMessage = "Method Not Allowed"),

    /**
     * 406 Not Acceptable
     */
    E406_NOT_ACCEPTABLE(httpStatusCode = 406, minorStatusCode = "000", errorMessage = "Not Acceptable"),

    /**
     * 409 Conflict
     */
    E409_CONFLICT(httpStatusCode = 409, minorStatusCode = "000", errorMessage = "Conflict"),

    E413_PAYLOAD_TOO_LARGE(httpStatusCode = 413, minorStatusCode = "000", errorMessage = "Payload is too Large"),
    E413_FILE_SIZE_TOO_LARGE(httpStatusCode = 413, minorStatusCode = "001", errorMessage = "File Size is Too Large"),

    /**
     * 406 Not Acceptable
     */
    E415_UNSUPPORTED_MEDIA_TYPE(
        httpStatusCode = 415,
        minorStatusCode = "000",
        errorMessage = "UnSupported MediaType"
    ),

    /**
     * 500 InternalServerError
     */
    E500_INTERNAL_SERVER_ERROR(httpStatusCode = 500, minorStatusCode = "000", errorMessage = "Internal Server Error"),
    E501_INIT_FAILED(httpStatusCode = 500, minorStatusCode = "001", errorMessage = "Init failed"),

    /**
     * 501 NotImplemented
     */
    E501_NOT_IMPLEMENTED(httpStatusCode = 501, minorStatusCode = "000", errorMessage = "Not Implemented"),

    /**
     * 503 Service UnAvailable
     */
    E503_SERVICE_UNAVAILABLE(httpStatusCode = 503, minorStatusCode = "000", errorMessage = "Service Unavailable"),

}
