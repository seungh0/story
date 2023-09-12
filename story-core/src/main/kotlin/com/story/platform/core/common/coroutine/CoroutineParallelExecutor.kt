package com.story.platform.core.common.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CoroutineParallelExecutor {

    suspend inline fun <E, R> executeDeferred(
        tasks: Collection<E>,
        batchSize: Int,
        concurrency: Int,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
        crossinline runnable: suspend (collection: Collection<E>) -> Collection<R>,
    ): Collection<R> = withContext(dispatcher) {
        val results = mutableListOf<R>()
        val deferredJobs = mutableListOf<Deferred<Collection<R>>>()
        tasks.chunked(batchSize)
            .forEach { chunkedData: List<E> ->
                deferredJobs += async { runnable.invoke(chunkedData) }

                if (deferredJobs.count() >= concurrency) {
                    results += deferredJobs.awaitAll().flatten()
                    deferredJobs.clear()
                }
            }
        results += deferredJobs.awaitAll().flatten()
        return@withContext results
    }

    suspend inline fun <E> executeJob(
        tasks: Collection<E>,
        batchSize: Int,
        concurrency: Int,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
        crossinline runnable: suspend (collection: Collection<E>) -> Unit,
    ) = withContext(dispatcher) {
        val jobs = mutableListOf<Job>()
        tasks.chunked(batchSize)
            .forEach { chunkedData: List<E> ->
                jobs += launch { runnable.invoke(chunkedData) }

                if (jobs.count() >= concurrency) {
                    jobs.joinAll()
                    jobs.clear()
                }
            }
    }

}
