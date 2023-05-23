package com.rhodes.republicservices.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit

/**
 * Implementation of DroverRepository interface
 */
class DriverRepositoryImpl(
    val retrofit: Retrofit,
    val driverDb: DriverDatabase
) : DriverRepository {

    private val service = retrofit.create(ApiDataService::class.java)

    override fun getApiData(): Flow<ApiData> {
       return flow {
           emit(service.getApiData())
       }.flowOn(Dispatchers.IO)
    }

    override fun getDrivers(): Flow<List<Driver>> {
        return flow {
            emit(driverDb.driverDao().getDrivers())
        }.flowOn(Dispatchers.IO)
    }

    override fun getDriver(driverId: Int): Flow<Driver> {
        return flow {
            emit(driverDb.driverDao().getDriver(driverId))
        }.flowOn(Dispatchers.IO)
    }

    override fun getRoutes(): Flow<List<Route>> {
       return flow {
           emit(driverDb.routeDao().getRoutes())
       }.flowOn(Dispatchers.IO)
    }

    override suspend fun insertDriver(driver: Driver) {
        driverDb.driverDao().insertDriver(driver)
    }

    override suspend fun insertRoute(route: Route) {
        driverDb.routeDao().insertRoute(route)
    }
}