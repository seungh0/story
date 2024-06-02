package com.story.core.domain.workspace

/**
 * - FREE Plan: 100TPS 제한
 * - PRO Plan: 500TPS 제한
 * - BUSINESS Plan: 1500TPS 제한
 * - ENTERPRISE Plan: 상의
 */
enum class WorkspacePricePlan(
    private val description: String,
) {

    FREE(description = "무료 플랜"),
    PRO(description = "프로"),
    BUSINESS(description = "비즈니스 플랜"),
    ENTERPRISE(description = "엔터프라이즈 플랜"),
    ;

}
