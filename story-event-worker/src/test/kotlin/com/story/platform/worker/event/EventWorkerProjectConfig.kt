package com.story.platform.worker.event

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

class EventWorkerProjectConfig : AbstractProjectConfig() {
    @ExperimentalKotest
    override var testCoroutineDispatcher = true
    override val coroutineDebugProbes = true
    override fun extensions() = listOf(SpringExtension)
}
