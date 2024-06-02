package com.story.core.common.distribution

enum class DistributionStrategy(
    val digit: Int,
    val pattern: String,
    val hashFormat: String,
) {

    TEN(digit = 1, pattern = "[0-9]", hashFormat = "%01d"),
    HUNDRED(digit = 2, pattern = "[0-9]{2}", hashFormat = "%02d"),
    THOUSAND(digit = 3, pattern = "[0-9]{3}", hashFormat = "%03d"),
    TEN_THOUSAND(digit = 4, pattern = "[0-9]{4}", hashFormat = "%04d"),
    HUNDRED_THOUSAND(digit = 5, pattern = "[0-9]{5}", hashFormat = "%05d"),
    ;

}
