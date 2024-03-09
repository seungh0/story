package com.story.worker

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringExtension

class WorkerProjectConfig : AbstractProjectConfig() {
    @ExperimentalKotest
    override var testCoroutineDispatcher = true
    override val coroutineDebugProbes = true
    override val isolationMode = IsolationMode.InstancePerTest
    override fun extensions() = listOf(SpringExtension)
}
