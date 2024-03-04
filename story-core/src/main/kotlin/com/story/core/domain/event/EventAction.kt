package com.story.core.domain.event

enum class EventAction(
    private val description: String,
) {

    CREATED(description = "생성"),
    MODIFIED(description = "수정"),
    REMOVED(description = "삭제"),
    ;

}
