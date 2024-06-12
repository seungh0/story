package com.story.api.config

import com.story.api.config.apikey.ApiKeyContextMethodArgumentResolver
import com.story.api.config.nonce.NonceMethodArgumentResolver
import com.story.core.common.json.Jsons
import com.story.core.support.cassandra.converter.PostIdReadConverter
import com.story.core.support.cassandra.converter.PostIdWriteConverter
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.format.FormatterRegistry
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class WebFluxConfig(
    private val authContextMethodArgumentResolver: ApiKeyContextMethodArgumentResolver,
    private val nonceMethodArgumentResolver: NonceMethodArgumentResolver,
    private val postKeyWriteConverter: PostIdWriteConverter,
    private val postKeyReadConverter: PostIdReadConverter,
) : WebFluxConfigurer {

    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.apply {
            addCustomResolver(authContextMethodArgumentResolver)
            addCustomResolver(nonceMethodArgumentResolver)
        }
    }

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(postKeyReadConverter)
        registry.addConverter(postKeyWriteConverter)
    }

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        val objectMapper = Jsons.DEFAULT_OBJECT_MAPPER

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
