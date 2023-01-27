package com.story.platform.core.support.spring

import com.story.platform.core.common.utils.LoggerUtilsExtension.log
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import java.util.function.Function

@DependsOn(value = ["applicationContextProvider"])
@Component
class SpringBeanProvider {

    fun <K, V> convertBeanMap(
        clazz: Class<V>,
        keyGenerator: Function<V, K>,
    ): Map<K, V> {
        val applicationContext: ApplicationContext = ApplicationContextProvider.applicationContext
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
