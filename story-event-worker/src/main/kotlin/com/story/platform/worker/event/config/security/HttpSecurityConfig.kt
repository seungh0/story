package com.story.platform.worker.event.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class HttpSecurityConfig {

    @Bean
    fun securityWebFilterChain(httpSecurity: ServerHttpSecurity): SecurityWebFilterChain = httpSecurity
        .csrf { csrf -> csrf.disable() }
        .formLogin { formLogin -> formLogin.disable() }
        .httpBasic {}
        .authorizeExchange { authorizedExchange ->
            authorizedExchange
                .pathMatchers("/monitoring/**").authenticated()
                .anyExchange().permitAll()
        }
        .build()

}
