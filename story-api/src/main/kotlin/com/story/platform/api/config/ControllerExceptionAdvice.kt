package com.story.platform.api.config

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException
import com.story.platform.core.common.logger.LoggerExtension.log
import com.story.platform.core.common.model.dto.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
class ControllerExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException::class)
    private fun handleWebExchangeBindException(exception: WebExchangeBindException): ApiResponse<Nothing> {
        val message = exception.bindingResult.fieldErrors.map { fieldError: FieldError -> "[${fieldError.field}] ${fieldError.defaultMessage}" }
        log.error("WebExchangeBindException: {}", message)
        return ApiResponse.fail(ErrorCode.E400_INVALID_ARGUMENTS, message)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServerWebInputException::class)
    private fun handleServerWebInputException(exception: ServerWebInputException): ApiResponse<Nothing> {
        log.warn(exception) { exception.message }
        return ApiResponse.fail(ErrorCode.E400_INVALID_ARGUMENTS)
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(MethodNotAllowedException::class)
    private fun handleMethodNotAllowedException(exception: MethodNotAllowedException): ApiResponse<Nothing> {
        log.warn(exception) { exception.message }
        return ApiResponse.fail(ErrorCode.E405_METHOD_NOT_ALLOWED)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    private fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException): ApiResponse<Nothing> {
        log.warn(exception.message)
        if (exception.rootCause is MismatchedInputException) {
            val parameterName = (exception.rootCause as MismatchedInputException).path.joinToString(separator = ",") { path -> path.fieldName }
            if (parameterName.isBlank()) {
                return ApiResponse.fail(ErrorCode.E400_INVALID_ARGUMENTS)
            }
            return ApiResponse.fail(ErrorCode.E400_INVALID_ARGUMENTS, setOf("($parameterName) is required."))
        }
        return ApiResponse.fail(ErrorCode.E400_INVALID_ARGUMENTS)
    }

    @ExceptionHandler(StoryBaseException::class)
    private fun handleBaseException(exception: StoryBaseException): ResponseEntity<ApiResponse<Nothing>> {
        log.error(exception) { exception.message }
        return ResponseEntity.status(exception.errorCode.httpStatusCode)
            .body(ApiResponse.fail(error = exception.errorCode))
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable::class)
    private fun handleException(exception: Exception): ApiResponse<Nothing> {
        log.error(exception) { exception.message }
        return ApiResponse.fail(ErrorCode.E500_INTERNAL_ERROR)
    }

}
