package com.story.core.common.warmer

import kotlinx.coroutines.sync.Mutex

abstract class ExactlyOnceRunWarmer : Warmer {

    override var isDone = false
    private val mutex = Mutex()

    override suspend fun run() {
        if (!isDone && mutex.tryLock()) {
            try {
                doRun()
                setDone()
            } finally {
                mutex.unlock()
            }
        }
    }

    protected fun setDone() {
        this.isDone = true
    }

    abstract suspend fun doRun()

}
