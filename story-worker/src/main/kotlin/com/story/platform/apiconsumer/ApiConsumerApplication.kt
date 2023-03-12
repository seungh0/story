package com.story.platform.apiconsumer

import com.story.platform.core.common.constants.StoryPackageConst.BASE_PACKAGE
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan(basePackages = [BASE_PACKAGE])
@SpringBootApplication(scanBasePackages = [BASE_PACKAGE])
class ApiConsumerApplication

fun main(args: Array<String>) {
    runApplication<ApiConsumerApplication>(*args)
}
