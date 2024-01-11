package com.story.core.domain.post.section

import com.story.core.common.json.toJson

interface PostSectionContent {

    fun makeData() = this.toJson()

}
