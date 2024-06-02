package com.story.core.common.distribution

object RangePartitioner {

    fun partition(startInclusive: Long, endInclusive: Long, partitionSize: Int): List<Long> {
        require(
            startInclusive <= endInclusive
        ) { "startInclusive($startInclusive)은 endInclusive($endInclusive)보다 클 수 없습니다" }

        require(partitionSize > 0) {
            "파티션 수($partitionSize)는 0보다 커야합니다"
        }

        val range = endInclusive - startInclusive + 1
        val quotient = range / partitionSize
        val remainder = range % partitionSize

        if (quotient <= 0) {
            return partition(
                startInclusive = startInclusive,
                endInclusive = endInclusive,
                partitionSize = remainder.toInt(),
            )
        }

        val result = mutableListOf<Long>()
        var mark = endInclusive

        repeat(partitionSize) { i ->
            result.add(mark)
            mark = if (i < remainder) {
                mark - quotient - 1
            } else {
                mark - quotient
            }
        }

        return result.reversed()
    }

}
