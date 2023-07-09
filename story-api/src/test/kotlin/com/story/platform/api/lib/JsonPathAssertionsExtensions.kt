package com.story.platform.api.lib

import org.springframework.test.web.reactive.server.JsonPathAssertions

fun JsonPathAssertions.isFalse() = this.isEqualTo(false)
fun JsonPathAssertions.isTrue() = this.isEqualTo(true)
