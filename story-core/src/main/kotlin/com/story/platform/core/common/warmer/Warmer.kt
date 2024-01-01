package com.story.platform.core.common.warmer

interface Warmer {
    suspend fun run()

    val isDone: Boolean
}
