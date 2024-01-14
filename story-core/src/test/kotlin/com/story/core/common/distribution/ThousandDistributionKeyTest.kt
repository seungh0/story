package com.story.core.common.distribution

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import java.util.Random

class ThousandDistributionKeyTest : StringSpec({

    "000~999 사이의 분산 키를 생성합니다" {
        // given
        val random = Random().nextInt().toString()

        // when
        val sut = ThousandDistributionKey.makeKey(random).key

        // then
        sut shouldHaveLength 3
        sut.toInt() shouldBeInRange 0..999
    }

    "000~999 사이의 모든 분산 키를 생성합니다" {
        // when
        val sut = ThousandDistributionKey.ALL_KEYS

        // then
        sut shouldHaveSize 1000
        sut.first().key shouldBe "000"
        sut.last().key shouldBe "999"
    }

})
