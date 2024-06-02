package com.story.core.infrastructure.spring

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class ApplicationContextProvider : ApplicationContextAware {

    override fun setApplicationContext(ctx: ApplicationContext) {
        applicationContext = ctx
    }

    companion object {
        fun getApplicationContext() = applicationContext

        private lateinit var applicationContext: ApplicationContext
    }

}
