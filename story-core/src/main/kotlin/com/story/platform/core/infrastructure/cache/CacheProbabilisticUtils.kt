package com.story.platform.core.infrastructure.cache

import java.time.Duration
import kotlin.random.Random

object CacheProbabilisticUtils {

    /**
     * Probabilistic Early Recomputation
     */
    fun isEarlyRecomputeRequired(
        currentTtl: Duration,
        expiryGap: Duration,
    ): Boolean {
        return (currentTtl.toMillis() - (Random.nextDouble(0.0, 1.0) * expiryGap.toMillis())) <= 0
    }

}
