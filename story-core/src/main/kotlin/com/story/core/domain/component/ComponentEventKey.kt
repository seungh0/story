package com.story.core.domain.component

import com.story.core.domain.event.EventKey

data class ComponentEventKey(
    val componentId: String,
) : EventKey {

    override fun makeKey(): String = "component::$componentId"

}
