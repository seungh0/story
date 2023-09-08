package com.story.platform.core.common.coroutine

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList

suspend fun <T> Flow<T>.toMutableList() = this.toList().toMutableList()
