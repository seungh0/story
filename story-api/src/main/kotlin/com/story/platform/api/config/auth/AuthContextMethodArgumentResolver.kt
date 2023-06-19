package com.story.platform.api.config.auth

import com.story.platform.core.common.error.InternalServerException
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthContextMethodArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(RequestAuthContext::class.java) && AuthContext::class.java == parameter.parameterType
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange,
    ): Mono<Any> {
        val authContext: AuthContext = exchange.getAttribute(AuthenticationHandlerFilter.AUTH_CONTEXT)
            ?: throw InternalServerException("AuthContext을 가져올 수 없습니다")
        return Mono.just(authContext)
    }

}
