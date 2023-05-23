package com.rhodes.republicservices.data

import kotlinx.serialization.Serializable
import retrofit2.http.GET

/**
 * Retrofit interface for remote API data to be retrieved and loaded into Room DB
 */
interface ApiDataService {
    @GET("/data")
    suspend fun getApiData(): ApiData
}

/**
 * Data models for the remote API data
 */
@Serializable
data class ApiData(
    val drivers: List<ApiDriver>,
    val routes: List<ApiRoute>
)

@Serializable
data class ApiDriver(
    val id: Int,
    val name: String
)

@Serializable
data class ApiRoute(
    val id: Int,
    val type: ApiRouteType,
    val name: String
)

@Serializable
enum class ApiRouteType {
    C,
    I,
    R
}