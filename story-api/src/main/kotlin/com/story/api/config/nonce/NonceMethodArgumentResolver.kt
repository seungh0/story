package com.story.api.config.nonce

import com.story.core.common.http.HttpHeader
import com.story.core.domain.nonce.NonceHeaderEmptyException
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class NonceMethodArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(RequestNonce::class.java) && String::class.java == parameter.parameterType
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange,
    ): Mono<Any> {
        val authContext: String? = exchange.request.headers.getFirst(HttpHeader.X_STORY_NONCE.header)

        val requestNonce = parameter.getParameterAnnotation(RequestNonce::class.java)
            ?: throw IllegalStateException("RequestNonce를 가져올 수 없습니다")

        if (authContext.isNullOrBlank() && requestNonce.required) {
            throw NonceHeaderEmptyException("Nonce가 필수로 있어야 합니다 methodName: ${parameter.method?.name}")
        }

        if (authContext.isNullOrBlank()) {
            return Mono.empty()
        }
        return Mono.just(authContext)
    }

}
