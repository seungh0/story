package com.story.platform.core.domain.event

import com.story.platform.core.common.sequence.SnowflakeIdGenerator
import com.story.platform.core.common.time.toEpochMilli
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class EventIdHelperTest : FunSpec({

    test("1분 단위로 슬롯팅한다") {
        // given
        val idGenerator = SnowflakeIdGenerator()
        val one = LocalDateTime.of(2023, 1, 1, 0, 1, 0)
        val two = LocalDateTime.of(2023, 1, 1, 0, 2, 0)
        val three = LocalDateTime.of(2023, 1, 1, 0, 2, 59)

        // when
        val slot1 = EventIdHelper.getSlot(idGenerator.nextId(timestamp = one.toEpochMilli()))
        val slot2 = EventIdHelper.getSlot(idGenerator.nextId(timestamp = two.toEpochMilli()))
        val slot3 = EventIdHelper.getSlot(idGenerator.nextId(timestamp = three.toEpochMilli()))

        // then
        slot2 shouldBe slot1 + 1
        slot2 shouldBe slot3
    }

    test("ID 범위의 슬롯 범위를 계산한다") {
        // given
        val idGenerator = SnowflakeIdGenerator()
        val one = LocalDateTime.of(2023, 1, 1, 0, 1, 0)
        val two = LocalDateTime.of(2023, 1, 1, 0, 10, 0)

        // when
        val range = EventIdHelper.getSlotRange(
            startId = idGenerator.nextId(timestamp = one.toEpochMilli()),
            endId = idGenerator.nextId(timestamp = two.toEpochMilli())
        )

        // then
        range.last - range.first shouldBe 9L
    }

})
