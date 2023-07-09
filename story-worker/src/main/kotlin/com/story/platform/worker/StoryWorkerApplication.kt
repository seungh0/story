package com.story.platform.worker

import com.story.platform.core.common.StoryPackageConst.BASE_PACKAGE
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan(basePackages = [BASE_PACKAGE])
@SpringBootApplication(scanBasePackages = [BASE_PACKAGE])
class StoryWorkerApplication

fun main(args: Array<String>) {
    runApplication<StoryWorkerApplication>(*args)
}
