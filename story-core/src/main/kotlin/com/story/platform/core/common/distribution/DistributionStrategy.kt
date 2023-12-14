package com.story.platform.core.common.distribution

enum class DistributionStrategy(
    val digit: Int,
    val pattern: String,
    val hashFormat: String,
) {

    SMALL(digit = 1, pattern = "[0-9]", hashFormat = "%01d"),
    MEDIUM(digit = 2, pattern = "[0-9]{2}", hashFormat = "%02d"),
    LARGE(digit = 3, pattern = "[0-9]{3}", hashFormat = "%03d"),
    X_LARGE(digit = 4, pattern = "[0-9]{4}", hashFormat = "%04d"),
    DOUBLE_X_LARGE(digit = 5, pattern = "[0-9]{5}", hashFormat = "%05d"),
    ;

}
