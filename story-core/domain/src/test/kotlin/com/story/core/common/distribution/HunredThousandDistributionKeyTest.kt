package com.story.core.common.distribution

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import java.util.Random

class HunredThousandDistributionKeyTest : StringSpec({

    "00000~99999 사이의 분산 키를 생성합니다" {
        // given
        val random = Random().nextInt().toString()

        // when
        val sut = HunredThousandDistributionKey.makeKey(random).key

        // then
        sut shouldHaveLength 5
        sut.toInt() shouldBeInRange 0..99999
    }

    "00000~99999 사이의 모든 분산 키를 생성합니다" {
        // when
        val sut = HunredThousandDistributionKey.ALL_KEYS

        // then
        sut shouldHaveSize 100000
        sut.first().key shouldBe "00000"
        sut.last().key shouldBe "99999"
    }

})
