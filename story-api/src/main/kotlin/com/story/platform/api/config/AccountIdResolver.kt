package com.story.platform.api.config

import com.story.platform.core.common.error.BadRequestException
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AccountIdResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AccountId::class.java) && String::class.java == parameter.parameterType
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange,
    ): Mono<Any> {
        return Mono.fromSupplier {
            val accountId = exchange.request.headers.getFirst(ACCOUNT_ID_HEADER)
            if (accountId.isNullOrBlank()) {
                throw BadRequestException("$ACCOUNT_ID_HEADER is Blank")
            }
            return@fromSupplier accountId
        }
    }

    companion object {
        private const val ACCOUNT_ID_HEADER = "X-Account-Id"
    }

}
