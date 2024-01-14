package com.story.api.application.post

data class PostOwnerApiResponse(
    val isOwner: Boolean,
    val ownerId: String,
) {

    companion object {
        fun of(ownerId: String, requestUserId: String?) = PostOwnerApiResponse(
            ownerId = ownerId,
            isOwner = ownerId == requestUserId,
        )
    }

}
