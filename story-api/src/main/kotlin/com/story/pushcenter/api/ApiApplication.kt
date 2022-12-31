package com.story.pushcenter.api

import com.story.pushcenter.core.common.constants.StoryPackageConst.BASE_PACKAGE
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan(basePackages = [BASE_PACKAGE])
@SpringBootApplication(scanBasePackages = [BASE_PACKAGE])
class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}
