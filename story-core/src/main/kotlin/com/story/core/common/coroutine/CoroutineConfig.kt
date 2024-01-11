package com.story.core.common.coroutine

import com.story.core.common.annotation.CpuBound
import com.story.core.common.annotation.IOBound
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoroutineConfig {

    @OptIn(ExperimentalCoroutinesApi::class)
    @IOBound
    @Bean
    fun dispatcherIO(): CoroutineDispatcher = Dispatchers.IO
        .limitedParallelism(parallelism = 200)

    @CpuBound
    @Bean
    fun dispatcherCPU(): CoroutineDispatcher = Dispatchers.Default

    companion object {
        const val DEFAULT_TIMEOUT_MS: Long = 3_000L
    }

}
