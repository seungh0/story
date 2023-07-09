package com.story.platform.core.common.logger

import mu.KLogger
import mu.KotlinLogging

object LoggerExtension {

    val log: KLogger
        inline get() = KotlinLogging.logger {}

}
