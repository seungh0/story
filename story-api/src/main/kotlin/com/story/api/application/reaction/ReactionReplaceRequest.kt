package com.story.api.application.reaction

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.Size

data class ReactionReplaceRequest(
    @field:Size(max = 20)
    val emotions: Set<ReactionEmotionUpsertRequest> = emptySet(),
) {

    @JsonIgnore
    fun isClearRequest() = this.emotions.isEmpty()

}
