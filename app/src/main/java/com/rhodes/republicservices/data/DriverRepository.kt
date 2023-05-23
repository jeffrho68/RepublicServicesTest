package com.rhodes.republicservices.data

import kotlinx.coroutines.flow.Flow

/**
 * Driver Repository interface
 */
interface DriverRepository {

    //Load data from API
    fun getApiData(): Flow<ApiData>

    //Local Room DB operations
    fun getDrivers(): Flow<List<Driver>>
    fun getDriver(driverId: Int): Flow<Driver>
    fun getRoutes(): Flow<List<Route>>
    suspend fun insertDriver(driver: Driver)
    suspend fun insertRoute(route: Route)
}