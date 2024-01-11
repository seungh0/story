package com.story.core.domain.component

enum class ComponentStatus(
    private val activated: Boolean,
) {

    ENABLED(true),
    DISABLED(false),
    ;

    fun isActivated(): Boolean = this.activated

}
