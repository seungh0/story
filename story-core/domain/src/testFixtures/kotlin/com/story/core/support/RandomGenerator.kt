package com.story.core.support

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.random.Random

object RandomGenerator {

    @JvmOverloads
    @JvmStatic
    fun generateString(
        start: Int = 1,
        end: Int = 10000,
    ) = "${Random.nextInt(start, end)}"

    @JvmStatic
    fun generateBoolean(): Boolean {
        return Random.nextInt(1, 2) % 2 == 0
    }

    @JvmOverloads
    @JvmStatic
    fun generateInt(
        start: Int = 1,
        end: Int = 10000,
    ) = Random.nextInt(start, end)

    @JvmOverloads
    @JvmStatic
    fun generateLong(
        start: Long = 1,
        end: Long = 10000,
    ) = Random.nextLong(start, end)

    @JvmOverloads
    @JvmStatic
    fun generateDouble(
        start: Double = 1.0,
        end: Double = 10000.0,
    ) = Random.nextDouble(start, end)

    @JvmOverloads
    @JvmStatic
    fun generateLocalDate(
        startYear: Int = 1998,
        endYear: Int = 2299,
        startMonth: Int = 1,
        endMonth: Int = 12,
        startDay: Int = 1,
        endDay: Int = 28,
    ): LocalDate = LocalDate.of(
        Random.nextInt(startYear, endYear),
        Random.nextInt(startMonth, endMonth),
        Random.nextInt(startDay, endDay)
    )

    @JvmOverloads
    @JvmStatic
    fun generateLocalTime(
        startHour: Int = 0,
        endHour: Int = 23,
        startTime: Int = 0,
        endTime: Int = 59,
    ): LocalTime = LocalTime.of(Random.nextInt(startHour, endHour), Random.nextInt(startTime, endTime))

    @JvmOverloads
    @JvmStatic
    fun generateLocalDateTime(
        startHour: Int = 0,
        endHour: Int = 23,
        startTime: Int = 0,
        endTime: Int = 59,
    ): LocalDateTime = LocalDateTime.of(
        generateLocalDate(),
        generateLocalTime(startHour = startHour, endHour = endHour, startTime = startTime, endTime = endTime)
    )

    @JvmStatic
    fun <T : Enum<T>> generateEnum(e: Class<T>): T = RandomEnumGenerator(e).randomEnum()

}
