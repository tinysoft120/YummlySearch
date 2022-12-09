package com.tinysoft.yummlysearch.repository

import androidx.annotation.WorkerThread
import com.tinysoft.yummlysearch.network.RestApiService
import com.tinysoft.yummlysearch.network.ResultWrapper
import com.tinysoft.yummlysearch.network.model.RecipeDetailResponse
import com.tinysoft.yummlysearch.network.model.SearchResponse
import kotlinx.coroutines.CancellationException
import retrofit2.Response

/**
 * A Repository is an interface that abstracts access to network data sources.
 */
interface Repository {
    /**
     * Get Yummly Search list via rest api.
     * Return result wrapper for Search response.
     * This method is suspendable synchronous.
     * @param query Search words
     * @param index Page index
     * @param maxResult Page limit count
     * @return Response wrapper
     */
    suspend fun getSearch(query: String, index: Int, maxResult: Int) : ResultWrapper<SearchResponse>

    /**
     * Get Yummly Item details information via rest api.
     * Return result wrapper for Details response.
     * This method is suspendable synchronous.
     * @param recipeId Recipe ID from search api
     * @return Response wrapper
     */
    suspend fun getDetails(recipeId: String): ResultWrapper<RecipeDetailResponse>
}

/**
 * Implementation class of Repository
 */
class RepositoryImpl(
    private val apiService: RestApiService,
) : Repository {

    @WorkerThread
    override suspend fun getSearch(query: String, index: Int, maxResult: Int): ResultWrapper<SearchResponse> {
        return apiCall { apiService.search(query, maxResult, index) }
    }

    @WorkerThread
    override suspend fun getDetails(recipeId: String): ResultWrapper<RecipeDetailResponse> {
        return apiCall { apiService.detail(recipeId) }
    }

    private suspend fun <T : Any> apiCall(call: suspend () -> Response<T>): ResultWrapper<T> {
        val response: Response<T>
        try {
            response = call.invoke()
        } catch (t: Throwable) {
            if (t is CancellationException) throw t
            // TODO: should handle error depend on Exception type. Ex: IOException, HttpException, SocketTimeoutException, ...
            return ResultWrapper.NetworkError
        }

        return if (!response.isSuccessful) {
            ResultWrapper.GenericError(response.code(), response.message())
        } else if (response.body() == null) {
            // Empty response error
            ResultWrapper.GenericError(1001, "response.body() can't be null")
        } else {
            ResultWrapper.Success(response.body()!!)
        }
    }
}