package com.story.platform.core

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

class ProjectConfig : AbstractProjectConfig() {
    @ExperimentalKotest
    override var testCoroutineDispatcher = true
    override val coroutineDebugProbes = true
    override fun extensions() = listOf(SpringExtension)
}
