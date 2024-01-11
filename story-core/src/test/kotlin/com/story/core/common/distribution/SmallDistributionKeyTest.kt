package com.story.core.common.distribution

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import java.util.Random

class SmallDistributionKeyTest : StringSpec({

    "0~9 사이의 분산 키를 생성합니다" {
        // given
        val random = Random().nextInt().toString()

        // when
        val sut = SmallDistributionKey.makeKey(random).key

        // then
        sut shouldHaveLength 1
        sut.toInt() shouldBeInRange 0..9
    }

    "0~9 사이의 모든 분산 키를 생성합니다" {
        // when
        val sut = SmallDistributionKey.ALL_KEYS

        // then
        sut shouldHaveSize 10
        sut.first().key shouldBe "0"
        sut.last().key shouldBe "9"
    }

})
