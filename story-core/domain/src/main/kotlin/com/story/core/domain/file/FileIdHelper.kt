package com.story.core.domain.file

import com.story.core.common.sequence.SnowflakeIdGenerator
import java.time.Duration

object FileIdHelper {

    private val BUCKET_SIZE = Duration.ofDays(1)

    fun getSlot(id: Long): Long {
        val timestamp: Long = id shr 22
        return (timestamp / BUCKET_SIZE.toMillis())
    }

    fun generate(): Long {
        val idGenerator = SnowflakeIdGenerator()
        return idGenerator.nextId()
    }

}
