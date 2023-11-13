package com.story.platform.core.domain.post.section

import com.story.platform.core.common.json.toJson

interface PostSectionContent {

    fun makeData() = this.toJson()

}
