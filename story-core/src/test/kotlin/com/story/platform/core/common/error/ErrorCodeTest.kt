package com.story.platform.core.common.error

import io.kotest.core.spec.style.StringSpec
import org.junit.jupiter.api.Assertions.fail

class ErrorCodeTest : StringSpec({

    "에러코드는 중복되지 않아야 한다" {
        // given
        val errorCodes = mutableSetOf<String>()

        // when & then
        for (errorCode in ErrorCode.values()) {
            if (errorCodes.contains(errorCode.code)) {
                fail<String>("중복되는 에러($errorCode)의 코드(${errorCode.code})가 존재합니다")
            }
            errorCodes += errorCode.code
        }
    }

})
