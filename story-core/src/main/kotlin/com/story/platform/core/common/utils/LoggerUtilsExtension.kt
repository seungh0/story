package com.story.platform.core.common.utils

import mu.KLogger
import mu.KotlinLogging

object LoggerUtilsExtension {

    val log: KLogger
        inline get() = KotlinLogging.logger {}

}
