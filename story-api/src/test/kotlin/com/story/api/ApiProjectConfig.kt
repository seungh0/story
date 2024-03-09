package com.story.api

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringExtension

class ApiProjectConfig : AbstractProjectConfig() {
    @ExperimentalKotest
    override var testCoroutineDispatcher = true
    override val coroutineDebugProbes = true
    override val isolationMode = IsolationMode.InstancePerTest

    @ExperimentalKotest
    override val concurrentTests = 2
    override fun extensions() = listOf(SpringExtension)
}
