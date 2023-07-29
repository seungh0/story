package com.story.platform.api.config.auth

import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.core.common.http.RequestIdGenerator
import com.story.platform.core.common.http.getRequestId
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@Component
class AuthenticationHandlerFilter(
    private val authenticationHandler: AuthenticationHandler,
) : CoWebFilter() {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        if (AuthenticationWhitelistChecker.checkNoAuthentication(
                method = exchange.request.method,
                path = exchange.request.uri.path
            )
        ) {
            return chain.filter(exchange)
        }
        val authentication = authenticationHandler.handleAuthentication(serverWebExchange = exchange)
        exchange.attributes[AUTH_CONTEXT] = AuthContext(
            workspaceId = authentication.workspaceId,
            requestId = exchange.getRequestId() ?: RequestIdGenerator.generate(),
        )
        return chain.filter(exchange = exchange)
    }

    companion object {
        const val AUTH_CONTEXT = "AUTH_CONTEXT"
    }

}
