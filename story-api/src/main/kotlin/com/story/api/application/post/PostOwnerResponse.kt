package com.story.api.application.post

data class PostOwnerResponse(
    val isOwner: Boolean,
    val ownerId: String,
) {

    companion object {
        fun of(ownerId: String, requestUserId: String?) = PostOwnerResponse(
            ownerId = ownerId,
            isOwner = ownerId == requestUserId,
        )
    }

}
