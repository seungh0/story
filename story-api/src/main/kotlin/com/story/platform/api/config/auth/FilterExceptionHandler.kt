package com.story.platform.api.config.auth

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.support.json.JsonUtils
import com.story.platform.core.support.logger.LoggerExtension.log
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class FilterExceptionHandler : ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, exception: Throwable): Mono<Void> {
        lateinit var result: ApiResponse<Nothing>
        var message = ""

        when (exception) {
            is StoryBaseException -> {
                result = ApiResponse.error(error = exception.errorCode)
                exchange.response.statusCode = HttpStatusCode.valueOf(exception.errorCode.httpStatusCode)
                exchange.response.rawStatusCode = exception.errorCode.httpStatusCode
            }

            is ResponseStatusException -> {
                result = ApiResponse.error(error = ErrorCode.E500_INTERNAL_SERVER_ERROR)
                exception.message.let { message = it }
                exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                exchange.response.rawStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()
            }

            else -> {
                result = ApiResponse.error(error = ErrorCode.E500_INTERNAL_SERVER_ERROR)
                exception.message?.let { message = it }
                exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                exchange.response.rawStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()
            }
        }

        log.error(message, exception)

        return exchange.response.writeWith(
            Mono.fromSupplier {
                val bufferFactory: DataBufferFactory = exchange.response.bufferFactory()
                JsonUtils.toJson(result).let { bufferFactory.wrap(it.toByteArray()) }
            }
        )
    }
}
