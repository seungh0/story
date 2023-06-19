package com.story.platform.api.config

import com.story.platform.core.support.json.JsonUtils
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class WebFluxConfig(
    private val authContextMethodArgumentResolver: AuthContextMethodArgumentResolver,
) : WebFluxConfigurer {

    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(authContextMethodArgumentResolver)
    }

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        val objectMapper = JsonUtils.DEFAULT_OBJECT_MAPPER

        configurer.defaultCodecs().jackson2JsonEncoder(
            Jackson2JsonEncoder(objectMapper)
        )
        configurer.defaultCodecs().jackson2JsonDecoder(
            Jackson2JsonDecoder(objectMapper)
        )
    }

    override fun getValidator(): Validator = LocalValidatorFactoryBean().apply {
        setValidationMessageSource(validationMessageSource())
    }

    @Bean
    fun validationMessageSource(): MessageSource = ReloadableResourceBundleMessageSource().apply {
        setBasename("classpath:/messages/validation")
        setDefaultEncoding("UTF-8")
        setCacheSeconds(60)
    }

}
