package com.story.core.common.distribution

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class RangePartitionerTest : FunSpec({

    test("전체 범위를 N개로 파티셔닝 한 마지막 기준 점을 가져온다") {
        // given
        val startInclusive = 1L

        // when
        val sut = RangePartitioner.partition(
            startInclusive = startInclusive,
            endInclusive = 100,
            partitionSize = 3,
        )

        // then
        sut shouldBe listOf(33L, 66L, 100L)
        sut[0] shouldBe 33
        sut[1] shouldBe 66
        sut[2] shouldBe 100
    }

    test("전체 1억건을 3개로 파티셔닝 하면 각각 33_333_333 ~ 33_333_334개 만큼씩 나뉜다") {
        // given
        val startInclusive = 1L

        // when
        val sut = RangePartitioner.partition(
            startInclusive = startInclusive,
            endInclusive = 100_000_000,
            partitionSize = 3,
        )

        // then
        sut shouldHaveSize 3
        sut[0] shouldBe 33_333_333
        sut[1] shouldBe 66_666_666
        sut[2] shouldBe 100_000_000
    }

    test("나누려는 파티션 수보다 전체 갯수가 작은 경우 전체 갯수 만큼의 파티션만 생긴다") {
        // when
        val sut = RangePartitioner.partition(
            startInclusive = 1,
            endInclusive = 2,
            partitionSize = 3,
        )

        // then
        sut shouldBe listOf(1L, 2L)
    }

    test("단 하나의 데이터만 있는 경우 한 개의 파티션이 생성된다") {
        // given
        val startInclusive = 1L

        // when
        val sut = RangePartitioner.partition(
            startInclusive = startInclusive,
            endInclusive = startInclusive,
            partitionSize = 3,
        )

        // then
        sut shouldBe listOf(1L)
    }

    test("시작 범위 값보다 종료 범위 값이 클 수 없다") {
        // when & then
        shouldThrowExactly<IllegalArgumentException> {
            RangePartitioner.partition(
                startInclusive = 3,
                endInclusive = 2,
                partitionSize = 3,
            )
        }
    }

    test("파티션 수가 0보다는 커야한다") {
        // given
        forAll(
            table(
                headers("numOfPartition"),
                row(0),
                row(-1),
                row(-2),
            )
        ) { numOfPartition: Int ->
            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                RangePartitioner.partition(
                    startInclusive = 3,
                    endInclusive = 2,
                    partitionSize = numOfPartition,
                )
            }
        }
    }

})