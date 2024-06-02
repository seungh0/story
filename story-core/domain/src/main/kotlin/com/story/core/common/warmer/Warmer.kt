package com.story.core.common.warmer

interface Warmer {
    suspend fun run()

    val isDone: Boolean
}
