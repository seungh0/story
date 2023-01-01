package com.story.datacenter.apiconsumer

import com.story.datacenter.core.common.constants.StoryPackageConst
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan(basePackages = [StoryPackageConst.BASE_PACKAGE])
@SpringBootApplication(scanBasePackages = [StoryPackageConst.BASE_PACKAGE])
class ApiConsumerApplication

fun main(args: Array<String>) {
    runApplication<ApiConsumerApplication>(*args)
}
