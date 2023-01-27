package com.story.platform.apiconsumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan(basePackages = [com.story.platform.core.common.constants.StoryPackageConst.BASE_PACKAGE])
@SpringBootApplication(scanBasePackages = [com.story.platform.core.common.constants.StoryPackageConst.BASE_PACKAGE])
class ApiConsumerApplication

fun main(args: Array<String>) {
    runApplication<ApiConsumerApplication>(*args)
}
