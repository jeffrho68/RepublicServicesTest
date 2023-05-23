package com.rhodes.republicservices.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rhodes.republicservices.R
import com.rhodes.republicservices.data.Driver
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * Shows the list of drivers and a button at the top right to sort by last name.
 */
@Composable
fun DriverScreen(
    viewModel: DriverViewModel,
    modifier: Modifier = Modifier,
    onNavigateToRouteScreen: () -> Unit = {}

) {
    val uiData by viewModel.uiData.collectAsState()

    //Needed to scroll the list to the top when sorting
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        //Button row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
           Button(
               onClick = {
                   //Sort the drivers and scroll to the top of the list
                   viewModel.sortDrivers()
                   coroutineScope.launch {
                       listState.animateScrollToItem(index = 0)
                   }
               }
           ) {
               Text(text = stringResource(id = R.string.sort_button))
           }
        }
        Spacer(modifier = Modifier.height(8.dp))
        DriverList(
            drivers = uiData.drivers,
            listState = listState,
            viewModel = viewModel,
            onNavigateToRouteScreen = onNavigateToRouteScreen
        )
    }
}

/**
 * The list of drivers
 */
@Composable
fun DriverList(
    drivers: List<Driver>,
    listState: LazyListState,
    viewModel: DriverViewModel,
    onNavigateToRouteScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = modifier
    ) {
        items(drivers) {
            DriverCard(
                driverId = it.id,
                driverName = "${it.firstName} ${it.lastName}",
                onClick = {
                    viewModel.getRoutesForDriver(it.id)
                    onNavigateToRouteScreen()
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

/**
 * Display the driver information in a clickable card. When clicked the user is navigated to the Route screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverCard(
    driverId: Int,
    driverName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
           //Id label / value
           LabelValueRow(R.string.id_label, driverId.toString())
           Spacer(modifier = Modifier.height(8.dp))
            //Name label / value
           LabelValueRow(R.string.name_label, driverName)
        }
    }
}

/**
 * A Row with a label and a value
 */
@Composable
fun LabelValueRow(
    @StringRes labelId: Int,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = stringResource(id = labelId))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Medium
        )
    }
}