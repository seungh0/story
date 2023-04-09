package com.story.platform.core.domain.post

enum class PostSpaceType(
    private val description: String,
) {

    ACCOUNT(description = "계정에 등록한 포스팅"),
    POST_COMMENT(description = "포스팅에 등록한 댓글"),
    POST_REPLY_COMMENT(description = "댓글에 등록한 답글"),

}
