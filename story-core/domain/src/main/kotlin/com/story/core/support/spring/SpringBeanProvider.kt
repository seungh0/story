package com.story.core.support.spring

import com.story.core.common.logger.LoggerExtension.log
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class SpringBeanProvider(
    private val applicationContext: ApplicationContext,
) {

    fun <K, V> convertBeanMap(
        clazz: Class<V>,
        keyGenerator: Function<V, K>,
    ): Map<K, V> {
        val applicationContextBeanMap: Map<String, V> = applicationContext.getBeansOfType(clazz)

        val beanMap = mutableMapOf<K, V>()
        for ((_, value) in applicationContextBeanMap) {
            val key = keyGenerator.apply(value)
            if (beanMap[key] != null) {
                log.error("이미 등록된 Bean 이 있습니다. {}", key)
                SpringApplication.exit(applicationContext)
            }
            beanMap[key] = value
        }
        return beanMap
    }

}
