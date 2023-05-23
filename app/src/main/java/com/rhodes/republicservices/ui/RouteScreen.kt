package com.rhodes.republicservices.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rhodes.republicservices.R

/**
 * Shows list of routes for the selected driver.
 */
@Composable
fun RouteScreen(
    viewModel: DriverViewModel,
    modifier: Modifier = Modifier,
) {
    val uiData by viewModel.uiData.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.routes_title, "${uiData.selectedDriver?.firstName} ${uiData.selectedDriver?.lastName}"),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier.height(8.dp))
        LazyColumn(
          contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            items(uiData.driverRoutes) {
                RouteCard(
                    routeType = it.type.name,
                    routeName = it.name,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * Displays the route information in a card
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteCard(
    routeType: String,
    routeName: String,
    modifier: Modifier = Modifier,
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            //Type label / value
            LabelValueRow(R.string.type_label, routeType)
            Spacer(modifier = Modifier.height(8.dp))
            //Name label / value
            LabelValueRow(R.string.name_label, routeName)
        }
    }
}