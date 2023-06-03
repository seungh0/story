package com.story.platform.core.support.logger

import mu.KLogger
import mu.KotlinLogging

object LoggerExtension {

    val log: KLogger
        inline get() = KotlinLogging.logger {}

}
