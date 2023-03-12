package com.story.platform.core.support.cache

import java.time.Duration
import kotlin.random.Random

object CachePerUtils {

    /**
     * Probablistic Early Recomputation
     */
    fun isEarlyRecomputeRequired(
        currentTtl: Duration,
        expiryGap: Duration,
    ): Boolean {
        return (currentTtl.toMillis() - (Random.nextDouble(0.0, 1.0) * expiryGap.toMillis())) <= 0
    }

}
