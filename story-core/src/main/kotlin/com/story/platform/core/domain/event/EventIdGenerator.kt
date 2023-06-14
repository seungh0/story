package com.story.platform.core.domain.event

import java.util.UUID

object EventIdGenerator {

    fun generate(): String {
        return VERSION + UUID.randomUUID().toString()
    }

    private const val VERSION = "v1-"

}
