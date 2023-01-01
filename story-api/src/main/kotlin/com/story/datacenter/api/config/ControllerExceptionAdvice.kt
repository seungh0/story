package com.story.datacenter.api.config

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.story.datacenter.core.common.error.ErrorCode.*
import com.story.datacenter.core.common.error.StoryBaseException
import com.story.datacenter.core.common.model.ApiResponse
import com.story.datacenter.core.common.utils.LoggerUtilsExtension.log
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebInputException
import java.util.stream.Collectors

@RestControllerAdvice
class ControllerExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException::class)
    private fun handleBadRequest(e: BindException): ApiResponse<Nothing> {
        val errorMessage = e.bindingResult.fieldErrors.stream()
            .map { fieldError -> fieldError.defaultMessage + " [${fieldError.field}]" }
            .collect(Collectors.joining("\n"))
        log.warn(errorMessage, e)
        return ApiResponse.fail(E400_BAD_REQUEST, errorMessage)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServerWebInputException::class)
    private fun handleServerWebInputException(e: ServerWebInputException): ApiResponse<Nothing> {
        log.warn(e.message, e)
        return ApiResponse.fail(E400_BAD_REQUEST)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    private fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ApiResponse<Nothing> {
        log.warn(e.message)
        if (e.rootCause is MissingKotlinParameterException) {
            val parameterName = (e.rootCause as MissingKotlinParameterException).parameter.name
            return ApiResponse.fail(
                E400_BAD_REQUEST,
                "Parameter ($parameterName) is Missing"
            )
        }
        return ApiResponse.fail(E400_BAD_REQUEST)
    }

    @ExceptionHandler(StoryBaseException::class)
    private fun handleBaseException(exception: StoryBaseException): ResponseEntity<ApiResponse<Nothing>> {
        log.error(exception.message, exception)
        return ResponseEntity.status(exception.errorCode.httpStatusCode)
            .body(ApiResponse.fail(error = exception.errorCode))
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    private fun handleInternalServerException(exception: Exception): ApiResponse<Nothing> {
        log.error(exception.message, exception)
        return ApiResponse.fail(E500_INTERNAL_SERVER_ERROR)
    }

}
