package com.story.platform.core.domain.event

import com.story.platform.core.common.sequence.SnowflakeIdGenerator
import java.time.Duration

object EventIdHelper {

    private val BUCKET_SIZE = Duration.ofMinutes(1)

    fun getSlot(snowflake: Long): Long {
        val timestamp: Long = snowflake shr 22
        return (timestamp / BUCKET_SIZE.toMillis())
    }

    fun getSlotRange(startId: Long, endId: Long): LongRange {
        return getSlot(startId)..getSlot(endId)
    }

    fun generate(): Long {
        val idGenerator = SnowflakeIdGenerator()
        return idGenerator.nextId()
    }

}
