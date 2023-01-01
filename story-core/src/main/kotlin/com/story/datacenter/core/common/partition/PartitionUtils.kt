package com.story.datacenter.core.common.partition

object PartitionUtils {

    fun <T> partition(
        list: List<T>,
        size: Int,
    ): List<List<T>> {
        val partitions: MutableList<List<T>> = arrayListOf()
        val length: Int = list.size
        if (length == 0) {
            return partitions
        }
        val numOfPartitions: Int = length / size + (if ((length % size == 0)) 0 else 1)
        for (i in 0 until numOfPartitions) {
            val from: Int = i * size
            val to: Int = (i * size + size).coerceAtMost(length)
            partitions.add(list.subList(from, to))
        }
        return partitions
    }

}

