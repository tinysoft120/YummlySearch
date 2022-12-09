package com.tinysoft.yummlysearch.network.model

data class RecipeDetailResponse(
    val attributes: AttributesX,
    val attribution: AttributionX,
    val flavors: FlavorsX,
    val id: String,
    val images: List<Image>,
    val ingredientLines: List<String>,
    val name: String,
    val numberOfServings: Int,
    val nutritionEstimates: List<NutritionEstimate>,
    val rating: Double,
    val source: Source,
    val totalTime: String,
    val totalTimeInSeconds: Int,
    val yield: String
)