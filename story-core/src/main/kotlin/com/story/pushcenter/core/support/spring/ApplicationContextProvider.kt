package com.story.pushcenter.core.support.spring

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class ApplicationContextProvider : ApplicationContextAware {

    override fun setApplicationContext(ctx: ApplicationContext) {
        applicationContext = ctx
    }

    companion object {
        lateinit var applicationContext: ApplicationContext
    }

}
