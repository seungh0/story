package com.story.core.support.spring

object SpringEnvironmentFinder {

    fun findProperty(key: String): String? =
        ApplicationContextProvider.getApplicationContext().environment.getProperty(key)

    fun findRequiredProperty(key: String): String =
        ApplicationContextProvider.getApplicationContext().environment.getRequiredProperty(key)

}
