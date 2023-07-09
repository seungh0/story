package com.story.platform.api.config

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException
import com.story.platform.core.common.logger.LoggerExtension.log
import com.story.platform.core.common.model.dto.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.ServerWebInputException
import java.util.stream.Collectors

@RestControllerAdvice
class ControllerExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException::class)
    private fun handleBadRequest(exception: BindException): ApiResponse<Nothing> {
        val errorMessage = exception.bindingResult.fieldErrors
            .mapNotNull { fieldError -> fieldError.defaultMessage?.plus(" [${fieldError.field}]") }
            .joinToString(separator = "\n")
        log.warn(exception) { errorMessage }
        return ApiResponse.fail(ErrorCode.E400_INVALID_ARGUMENTS, errorMessage)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException::class)
    private fun handleWebExchangeBindException(exception: WebExchangeBindException): ApiResponse<Nothing> {
        val errorMessage = exception.bindingResult.fieldErrors.stream()
            .map { fieldError: FieldError -> fieldError.field + " " + fieldError.defaultMessage }
            .collect(Collectors.joining("\n"))
        log.error("WebExchangeBindException: {}", errorMessage)
        return ApiResponse.fail(ErrorCode.E400_INVALID_ARGUMENTS, errorMessage)
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
        log.warn(exception) { exception.message }
        if (exception.rootCause is MissingKotlinParameterException) {
            val parameterName = (exception.rootCause as MissingKotlinParameterException).parameter.name
            return ApiResponse.fail(
                ErrorCode.E400_INVALID_ARGUMENTS,
                "Parameter ($parameterName) is Missing"
            )
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
