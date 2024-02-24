package com.story.core.domain.file

import io.kotest.core.spec.style.StringSpec

class FileIdHelperTest : StringSpec({

    "abc" {
        val fileId = FileIdHelper.generate()
        println(fileId)
        println(FileIdHelper.getSlot(fileId))
    }

})
