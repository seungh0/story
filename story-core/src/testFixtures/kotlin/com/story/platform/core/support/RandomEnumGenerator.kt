package com.story.platform.core.support

import java.security.SecureRandom

class RandomEnumGenerator<T : Enum<T>>(e: Class<T>) {

    private val values: Array<T>

    init {
        values = e.enumConstants
    }

    fun randomEnum(): T {
        return values[random.nextInt(values.size)]
    }

    companion object {
        private val random = SecureRandom()
    }

}
