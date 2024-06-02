package com.story.core.common.distribution

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

internal class SlotAllocatorTest : FunSpec({

    context("슬롯 크기만큼 ID를 슬롯에 할당한다") {
        test("1번에 대한 슬롯") {
            // given
            val id = 1L

            // when
            val slot = SlotAssigner.assign(seq = id, firstSlotId = 1L, slotSize = 500)

            // then
            slot shouldBe 1L
        }

        test("500번에 대한 슬롯") {
            // given
            val id = 500L

            // when
            val slot = SlotAssigner.assign(seq = id, firstSlotId = 1L, slotSize = 500)

            // then
            slot shouldBe 1L
        }

        test("501번에 대한 슬롯") {
            // given
            val id = 501L

            // when
            val slot = SlotAssigner.assign(seq = id, firstSlotId = 1L, slotSize = 500)

            // then
            slot shouldBe 2L
        }

        test("ID가 1보다 작을 수 없다") {
            // given
            val id = 0L

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                SlotAssigner.assign(seq = id, firstSlotId = 1L, slotSize = 500)
            }
        }
    }

})
