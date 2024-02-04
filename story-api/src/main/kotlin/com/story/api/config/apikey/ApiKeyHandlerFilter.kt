package com.story.api.config.apikey

import com.story.api.application.apikey.ApiKeyHandler
import com.story.api.application.workspace.WorkspaceRetrieveHandler
import com.story.core.common.http.RequestIdGenerator
import com.story.core.common.http.getRequestId
import com.story.core.common.http.getRequestUserId
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@Component
class ApiKeyHandlerFilter(
    private val apiKeyHandler: ApiKeyHandler,
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) : CoWebFilter() {

    override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
        if (ApiKeyWhitelistChecker.checkNoApiKey(
                method = exchange.request.method,
                path = exchange.request.uri.path
            )
        ) {
            return chain.filter(exchange)
        }
        val apiKey = apiKeyHandler.handleApiKey(serverWebExchange = exchange)

        workspaceRetrieveHandler.validateEnabledWorkspace(workspaceId = apiKey.workspaceId)

        exchange.attributes[API_KEY_CONTEXT] = ApiKeyContext(
            workspaceId = apiKey.workspaceId,
            requestId = exchange.getRequestId() ?: RequestIdGenerator.generate(),
            requestUserId = exchange.getRequestUserId(),
        )
        return chain.filter(exchange = exchange)
    }

    companion object {
        const val API_KEY_CONTEXT = "API_KEY_CONTEXT"
    }

}
