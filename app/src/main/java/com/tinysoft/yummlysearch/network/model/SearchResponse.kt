package com.tinysoft.yummlysearch.network.model

data class SearchResponse(
    val attribution: Attribution?,
    val criteria: Criteria?,
    val matches: List<Matche>,
    val totalMatchCount: Int
)