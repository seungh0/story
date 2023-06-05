package com.story.platform.core.common.utils

import java.util.UUID

object EventIdGenerator {

    fun generate(): String {
        return VERSION + UUID.randomUUID().toString()
    }

    private const val VERSION = "v1-"

}
