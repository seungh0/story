package com.story.api.config.apikey

import com.story.core.common.error.InternalServerException
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class ApiKeyContextMethodArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(RequestApiKey::class.java) && ApiKeyContext::class.java == parameter.parameterType
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange,
    ): Mono<Any> {
        val authContext: ApiKeyContext = exchange.getAttribute(ApiKeyHandlerFilter.API_KEY_CONTEXT)
            ?: throw InternalServerException("[예상치 못한 에러가 발생하였습니다] AuthContext을 가져올 수 없습니다")
        return Mono.just(authContext)
    }

}
