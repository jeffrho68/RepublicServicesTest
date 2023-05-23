package com.rhodes.republicservices.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhodes.republicservices.data.Driver
import com.rhodes.republicservices.data.DriverRepository
import com.rhodes.republicservices.data.Route
import com.rhodes.republicservices.data.RouteType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the app
 */
class DriverViewModel(
    private val repository: DriverRepository
) : ViewModel() {

    //UI State info (isLoading, isError, etc)
    private val _uiState = MutableStateFlow(ViewState())
    val uiState: StateFlow<ViewState> = _uiState.asStateFlow()

    //UI Data info - data to be displayed on the screen
    private val _uiData = MutableStateFlow(DriverUIData())
    val uiData: StateFlow<DriverUIData> = _uiData.asStateFlow()

    init {
        loadData()
    }

    private fun setLoading(isLoading: Boolean) {
        _uiState.update {
            it.copy( isLoading = isLoading )
        }
    }

    /**
     * Retrieve data from API and load into Room DB
     */
    private fun loadData() {
        viewModelScope.launch {
            repository.getApiData()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isError = true,
                            errorMessage = throwable.message
                        )
                    }
                }
                .onStart { setLoading(true) }
                .onCompletion { setLoading(false) }
                .collect { apiData ->
                    //Insert API Drivers into Room DB
                    // Need to split API name into lastName, firstName
                    val drivers = apiData.drivers.map { apiDriver ->
                        val splitName = apiDriver.name.split(" ")
                        Driver(
                            id = apiDriver.id,
                            firstName = splitName[0],
                            lastName = splitName[1]
                        )
                    }.onEach { insertDriver(it) }

                    //Insert API Routes into Room DB
                    apiData.routes.map { apiRoute ->
                        Route(
                            id = apiRoute.id,
                            type = RouteType.valueOf(apiRoute.type.name),
                            name = apiRoute.name
                        )
                    }.forEach { insertRoute(it) }

                    _uiData.update {
                        it.copy(
                            drivers = drivers
                        )
                    }
                }
        }
    }

    private fun insertDriver(driver: Driver) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertDriver(driver)
        }
    }
    private fun insertRoute(route: Route) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertRoute(route)
        }
    }

    fun sortDrivers() {
        viewModelScope.launch {
            repository.getDrivers().collect {
                _uiData.update { uiData ->
                    uiData.copy(
                        drivers = uiData.drivers.sortedBy { it.lastName }
                    )
                }
            }
        }
    }

    fun getRoutesForDriver(driverId: Int) {
        viewModelScope.launch {
            combine(
                repository.getDriver(driverId),
                repository.getRoutes()
            ) { driver, routes ->  driver to routes }
            .collect { (driver, routes) ->
                val driverRoutes = mutableListOf<Route>()

                val equalRoute = routes.firstOrNull { it.id == driverId }
                val isDivisibleBy2 = driverId % 2 == 0
                val isDivisibleBy5 = driverId % 5 == 0

                //If there is a route.id == driverId add the route
                equalRoute?.let { driverRoutes.add(it) }

                //If driverId is divisible by 2, add the first R route
                if (isDivisibleBy2) {
                    driverRoutes.add(routes.first { it.type == RouteType.R })
                }
                //If driverId is divisible by 5, add the second C route
                if (isDivisibleBy5) {
                    driverRoutes.add(routes.filter { it.type == RouteType.C }[1])
                }
                //If driver has no equal route and not divisible by 2 or 5, show the last I route
                if (equalRoute == null && !isDivisibleBy2 && !isDivisibleBy5){
                    driverRoutes.add(routes.last { it.type == RouteType.I } )
                }
                _uiData.update { uiData ->
                    uiData.copy(
                        selectedDriver = driver,
                        driverRoutes = driverRoutes
                    )
                }
            }
        }
    }

    //View State - Loading or error. Used by RepublicServicesApp main screen
    data class ViewState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val errorMessage: String? = null
    )

    //UI Data -  this is data to be displayed by the Driver and Route screens
    data class DriverUIData(
        val drivers: List<Driver> = emptyList(),
        val selectedDriver: Driver? = null,
        val driverRoutes: List<Route> = emptyList()
    )
}