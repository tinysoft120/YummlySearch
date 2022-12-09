package com.tinysoft.yummlysearch.network.model

data class NutritionEstimate(
    val attribute: String,
    val description: String,
    val unit: Unit,
    val value: Double
)