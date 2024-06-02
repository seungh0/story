package com.story.core.common.distribution

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import java.util.Random

class HundredDistributionKeyTest : StringSpec({

    "0~9 사이의 분산 키를 생성합니다" {
        // given
        val random = Random().nextInt().toString()

        // when
        val sut = HundredDistributionKey.makeKey(random).key

        // then
        sut shouldHaveLength 2
        sut.toInt() shouldBeInRange 0..99
    }

    "00~99 사이의 모든 분산 키를 생성합니다" {
        // when
        val sut = HundredDistributionKey.ALL_KEYS

        // then
        sut shouldHaveSize 100
        sut.first().key shouldBe "00"
        sut.last().key shouldBe "99"
    }

})
