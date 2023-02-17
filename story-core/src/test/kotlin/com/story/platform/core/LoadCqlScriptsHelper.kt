package com.story.platform.core

import com.story.platform.core.domain.post.PostTableNames

object LoadCqlScriptsHelper {

    val POST_REVERSE_V1: String = """
        CREATE TABLE IF NOT EXISTS ${PostTableNames.POST_REVERSE}
        (
            service_type text,
            account_id   text,
            post_id      bigint,
            space_type   text,
            space_id     text,
            title        text,
            content      text,
            extra_json   text,
            PRIMARY KEY ((service_type, account_id), post_id, space_type, space_id)
        )
        WITH CLUSTERING ORDER BY
        (
            post_id DESC,
            space_type DESC,
            space_id DESC
        )
    """.trimIndent()

}
