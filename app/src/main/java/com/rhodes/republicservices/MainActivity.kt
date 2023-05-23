package com.rhodes.republicservices

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rhodes.republicservices.ui.DriverScreen
import com.rhodes.republicservices.ui.DriverViewModel
import com.rhodes.republicservices.ui.RouteScreen
import com.rhodes.republicservices.ui.theme.RepublicServicesTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RepublicServicesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RepublicServicesApp()
                }
            }
        }
    }
}

/**
 * Navigation routes - one for each screen
 */
enum class AppScreen(@StringRes val title: Int) {
    Driver(R.string.driver_screen_title),
    Route(R.string.route_screen_title)
}

/**
 * Top App bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = stringResource(id = currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

/**
 * Top-level screen with the AppBar and NavHost. Navhost will contain the Driver and Route screens
 * and handle navigation between them.
 * Also handles isLoading and isError uiState
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepublicServicesApp(
    viewModel: DriverViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Driver.name
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {

            NavHost(
                navController = navController,
                startDestination = AppScreen.Driver.name,
            ) {
                composable(route = AppScreen.Driver.name) {
                    DriverScreen(
                        viewModel = viewModel,
                        onNavigateToRouteScreen = { navController.navigate(route = AppScreen.Route.name) }
                    )
                }
                composable(route = AppScreen.Route.name) {
                    RouteScreen(viewModel = viewModel )
                }
            }

            if (uiState.isLoading) {
                LoadingView()
            }

            if (uiState.isError) {
                ErrorView(
                    errorMessage = uiState.errorMessage ?: stringResource(id = R.string.generic_error_message)
                )
            }
        }
    }
}

/**
 * Shows the loading view  - Circle progress in center of the entire screen
 */
@Composable
fun LoadingView(
    modifier: Modifier = Modifier
) {
    Surface {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * Shows the Error View that will be shown if the service API call fails. Shows the error message
 * and as button to finish the app.
 */
@Composable
fun ErrorView(
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as? Activity)
    Surface {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { activity?.finish() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text( text = "finish")
            }
        }
    }
}



