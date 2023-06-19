package com.story.platform.api.config

import com.story.platform.api.domain.authentication.AuthenticationHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@Component
class AuthFilter(
    private val authenticationHandler: AuthenticationHandler,
) : CoWebFilter() {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        if (AuthWhiteListChecker.checkNoAuthentication(exchange.request.uri.path)) {
            return chain.filter(exchange)
        }
        val authentication = authenticationHandler.handleAuthentication(serverWebExchange = exchange)
        exchange.attributes[AUTH_CONTEXT] = AuthContext(
            serviceType = authentication.serviceType,
        )
        return chain.filter(exchange)
    }

    companion object {
        const val AUTH_CONTEXT = "AUTH_CONTEXT"
    }

}
