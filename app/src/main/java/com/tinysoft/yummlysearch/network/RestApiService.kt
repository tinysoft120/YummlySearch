package com.tinysoft.yummlysearch.network

import com.tinysoft.yummlysearch.network.model.RecipeDetailResponse
import com.tinysoft.yummlysearch.network.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestApiService {
    @GET("api/recipes")
    suspend fun search(
        @Query("q") query: String,
        @Query("maxResult") maxResult: Int,
        @Query("start") start: Int
    ): Response<SearchResponse>

    @GET("api/recipe/{recipe_id}")
    suspend fun detail(
        @Path("recipe_id") recipeId: String
    ): Response<RecipeDetailResponse>
}