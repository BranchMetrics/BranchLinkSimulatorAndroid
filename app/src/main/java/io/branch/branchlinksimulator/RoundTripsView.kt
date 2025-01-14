package io.branch.branchlinksimulator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

@Composable
fun RoundTripsNavHost(navController: NavHostController, roundTripStore: RoundTripStore) {
    NavHost(navController = navController, startDestination = "requestList") {
        composable("requestList") {
            RoundTripsView(navController, roundTripStore)
        }
        composable(
            "detail/{roundTripId}",
            arguments = listOf(navArgument("roundTripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roundTripId = backStackEntry.arguments?.getString("roundTripId")
            val roundTrip = roundTripStore.roundTrips.collectAsState().value.find { it.id == roundTripId }
            if (roundTrip != null) {
                RoundTripDetailView(roundTrip)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoundTripsView(navController: NavHostController, viewModel: RoundTripStore) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Requests") })
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(viewModel.roundTrips.value) { roundTrip ->
                    RoundTripViewItem(navController, roundTrip)
                    Divider(color = Color.Gray, thickness = 1.dp)
                }
            }
        }
    )
}

@Composable
fun RoundTripViewItem(navController: NavHostController, roundTrip: RoundTrip) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("detail/${roundTrip.id}") }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(roundTrip.url, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
            Text(
                roundTrip.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            roundTrip.response?.let {
                Text(
                    "Status Code: ${it.statusCode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun RoundTripDetailView(roundTrip: RoundTrip) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                VariableView(label = "URL", value = roundTrip.url)
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Text("Request", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            item {
                VariableView(label = "Body", value = roundTrip.request?.body ?: "FAILED")
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                roundTrip.response?.let {
                    Text("Response", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Text("Status Code: ${it.statusCode}", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    VariableView(label = "Body", value = it.body)
                }
            }
        }

    }
}

@Composable
fun VariableView(label: String, value: String) {
    val localClipboardManager = LocalClipboardManager.current

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("$label:", style = TextStyle(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
            Button(onClick = { copyToClipboard(localClipboardManager, value) }) {
                Text("Copy")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground)
    }
}


