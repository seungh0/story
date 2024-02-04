package com.story.core.common.error

enum class ErrorCode(
    val httpStatusCode: Int,
    val code: String,
    val description: String,
) {

    /**
     * 400 BadRequest
     */
    E400_INVALID_ARGUMENTS(
        httpStatusCode = 400,
        code = "invalid_arguments",
        description = "필수 파라미터가 없거나, 파라미터가 유효하지 않는 경우"
    ),
    E400_INACTIVATED_WORKSPACE(httpStatusCode = 400, code = "inactivated_workspace", description = "비활성화된 워크스페이스인 경우"),
    E400_INVALID_NONCE(httpStatusCode = 400, code = "invalid_nonce", description = "유효하지 않은 Nonce인 경우"),
    E400_MISSING_REQUEST_USER_ID(
        httpStatusCode = 400,
        code = "missing_request_user_id",
        description = "요청자(\"X-Request-User-Id\") 헤더가 없는 경우"
    ),
    E400_INVALID_EVENT_KEY(httpStatusCode = 400, code = "invalid_event_key", description = "유효하지 않은 EventKey인 경우"),
    E400_NONCE_HEADER_EMPTY(
        httpStatusCode = 400,
        code = "nonce_header_empty",
        description = "X-Story-Nonce 헤더가 비어 있는 경우 (Nonce 키가 필수인 경우)"
    ),

    /**
     * 401 UnAuthorized
     */
    E401_INVALID_API_KEY(
        httpStatusCode = 401,
        code = "invalid_api_key",
        description = "유효하지 않은 API 키가 요청된 경우"
    ),
    E401_INACTIVATED_API_KEY(
        httpStatusCode = 401,
        code = "inactivated_api_key",
        description = "비활성화된 API 키가 요청된 경우"
    ),
    E401_EMPTY_API_KEY(
        httpStatusCode = 401,
        code = "empty_api_key",
        description = "API-Key 헤더(\"X-Story-Api-Key\")가 없는 경우"
    ),

    /**
     * 403 Forbidden
     */
    E403_NO_PERMISSION(httpStatusCode = 403, code = "no_permission", description = "권한이 없는 경우"),
    E403_FEED_MAPPING_CAPACITY_EXCEEDED(
        httpStatusCode = 403,
        code = "feed_mapping_capacity_exceed",
        description = "최대 허용할 수 있는 피드 매핑 갯수를 초과하는 경우"
    ),
    E403_WORKSPACE_NO_PERMISSION(
        httpStatusCode = 403,
        code = "workspace_no_permission",
        description = "워크스페이스의 권한이 없는 경우"
    ),
    E403_EMOTION_COUNT_LIMIT_EXCEEDED(
        httpStatusCode = 403,
        code = "emotion_count_limit_exceeded",
        description = "최대 등록할 수 있는 이모션 갯수를 초과하는 경우"
    ),

    /**
     * 404 NotFound
     */
    E404_NOT_EXISTS_API_KEY(
        httpStatusCode = 404,
        code = "not_exists_api_key",
        description = "존재하지 않는 API-Key인 경우 발생"
    ),
    E404_NOT_EXISTS_COMPONENT(httpStatusCode = 404, code = "not_exists_component", description = "존재하지 않는 컴포넌트인 경우"),
    E404_NOT_EXISTS_RESOURCE(httpStatusCode = 404, code = "not_exists_resource", description = "존재하지 않는 리소스인 경우"),
    E404_NOT_EXISTS_POST(httpStatusCode = 404, code = "not_exists_post", description = "존재하지 않는 포스트인 경우"),
    E404_NOT_EXISTS_CONNECT_FEED_MAPPING(
        httpStatusCode = 404,
        code = "not_exists_connect_mapping_feed",
        description = "존재하지 않는 피드 매핑 설정인 경우"
    ),
    E404_NOT_EXISTS_WORKSPACE(
        httpStatusCode = 404,
        code = "not_exists_workspace",
        description = "존재하지 않는 워크스페이스인 경우"
    ),
    E404_NOT_EXISTS_EMOTION(httpStatusCode = 404, code = "not_exists_emotion", description = "존재하지 않는 이모션인 경우"),
    E404_NOT_EXISTS_FEED(httpStatusCode = 404, code = "not_exists_feed", description = "존재하지 않는 피드인 경우"),
    E404_NOT_EXISTS_FEED_MAPPING(
        httpStatusCode = 404,
        code = "not_exists_feed_mapping",
        description = "존재하지 않는 피드 매핑인 경우"
    ),

    /**
     * 405 Method Not Allowed
     */
    E405_METHOD_NOT_ALLOWED(
        httpStatusCode = 405,
        code = "method_not_allowed",
        description = "허용하지 않는 HTTP Method인 경우"
    ),

    /**
     * 409 Conflict
     */
    E409_ALREADY_EXISTS_API_KEY(
        httpStatusCode = 409,
        code = "already_exists_api_key",
        description = "API-Key 중복 시"
    ),
    E409_ALREADY_EXISTS_COMPONENT(
        httpStatusCode = 409,
        code = "already_exists_component",
        description = "컴포넌트 중복 시"
    ),
    E409_ALREADY_CONNECTED_FEED_MAPPING(
        httpStatusCode = 409,
        code = "already_connected_feed_mapping",
        description = "피드 매핑 중복 시"
    ),
    E409_ALREADY_EXISTS_EMOTION(httpStatusCode = 409, code = "already_exists_emotion", description = "이모션 중복 시"),

    /**
     * 429 Too Many Request
     */
    E429_TOO_MANY_REQUEST(httpStatusCode = 429, code = "too_many_request", description = "일시적으로 제한을 넘어서서 요청 시"),

    /**
     * 500 Internal Server Error
     */
    E500_INTERNAL_ERROR(httpStatusCode = 500, code = "internal_error", description = "서버 내부적으로 문제 발생 시"),

    /**
     * 501 NotImplemented
     */
    E501_NOT_SUPPORTED(httpStatusCode = 501, code = "not_supported", description = "지원하지 않는 요청인 경우"),

    /**
     * 503 Service UnAvailable
     */
    E503_SERVICE_UNAVAILABLE(httpStatusCode = 503, code = "service_unavailable", description = "현재 서비스를 이용할 수 없는 경우"),

}
