package com.story.datacenter.core.common.utils

import mu.KLogger
import mu.KotlinLogging

object LoggerUtilsExtension {

    val log: KLogger inline get() = KotlinLogging.logger {}

}
