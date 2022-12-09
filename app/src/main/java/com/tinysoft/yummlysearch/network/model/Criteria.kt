package com.tinysoft.yummlysearch.network.model

data class Criteria(
    val allowedIngredient: Any,
    val excludedIngredient: Any,
    val q: String
)