package com.story.platform.core.common.distribution

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import java.util.Random

class XLargeDistributionKeyTest : StringSpec({

    "0000~9999 사이의 분산 키를 생성합니다" {
        // given
        val random = Random().nextInt().toString()

        // when
        val sut = XLargeDistributionKey.makeKey(random).key

        // then
        sut shouldHaveLength 4
        sut.toInt() shouldBeInRange 0..9999
    }

    "0000~9999 사이의 모든 분산 키를 생성합니다" {
        // when
        val sut = XLargeDistributionKey.ALL_KEYS

        // then
        sut shouldHaveSize 10000
        sut.first().key shouldBe "0000"
        sut.last().key shouldBe "9999"
    }

})
