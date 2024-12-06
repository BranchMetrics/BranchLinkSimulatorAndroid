package io.branch.branchlinksimulator

import androidx.compose.ui.platform.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.system.exitProcess

const val SELECTED_CONFIG_NAME = "selectedConfigName"
const val PREFERENCES_KEY = "ApiPreferences"

@Composable
fun ApiSettingsPanel(navController: NavController) {
    val context = LocalContext.current
    val preferences = remember { context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE) }
    var selectedConfig by remember { mutableStateOf(ApiConfigManager.loadConfigOrDefault(preferences)) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        ApiInfoRow(label = "Branch Key", value = selectedConfig.branchKey)
        ApiInfoRow(label = "API URL", value = selectedConfig.apiUrl)
        ApiInfoRow(label = "App ID", value = selectedConfig.appId)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                ApiButton(configName = STAGING, selectedConfig = selectedConfig, Modifier.weight(1f), onSelect = {
                    selectedConfig = it
                    saveConfig(preferences, it)
                })
                ApiButton(configName = PRODUCTION, selectedConfig = selectedConfig, Modifier.weight(1f), onSelect = {
                    selectedConfig = it
                    saveConfig(preferences, it)
                })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                ApiButton(configName = STAGING_AC, selectedConfig = selectedConfig, Modifier.weight(1f), onSelect = {
                    selectedConfig = it
                    saveConfig(preferences, it)
                })
                ApiButton(configName = PRODUCTION_AC, selectedConfig = selectedConfig, Modifier.weight(1f), onSelect = {
                    selectedConfig = it
                    saveConfig(preferences, it)
                })
            }
            RoundedButton(title = "See Requests", icon = R.drawable.branch_badge_all_white) {
                navController.navigate("request")
            }
        }
    }
}

@Composable
fun ApiInfoRow(label: String, value: String) {
    val localClipboardManager = LocalClipboardManager.current

    Row(
        modifier = Modifier
            .background(Color.LightGray, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Black
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
        }

        Button(
            onClick = { copyToClipboard(localClipboardManager, value) },
        ) {
            Text("Copy")
        }
    }
}


@Composable
fun ApiButton(
    configName: String,
    selectedConfig: ApiConfiguration,
    modifier: Modifier = Modifier,
    onSelect: (ApiConfiguration) -> Unit
) {
    val config = apiConfigurationsMap[configName]
    val isSelected = selectedConfig == config

    var showDialog by remember { mutableStateOf(false) }

    Button(
        onClick = {
            if (config != null) {
                onSelect(config)
                showDialog = true
            }
        },
        modifier = modifier.padding(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
            contentColor = Color.White
        )
    ) {
        Text(
            text = configName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "Configuration Changed")
            },
            text = {
                Text(text = "You need to restart the app for the changes to take effect.")
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(onClick = { exitProcess(0) }) {
                    Text("OK")
                }
            }
        )
    }
}

fun saveConfig(preferences: SharedPreferences, config: ApiConfiguration) {
    val configName = apiConfigurationsMap.entries.firstOrNull { it.value == config }?.key ?: PRODUCTION
    preferences.edit().putString(SELECTED_CONFIG_NAME, configName).apply()
}

fun copyToClipboard(manager: ClipboardManager, text: String) {
    manager.setText(AnnotatedString(text))
}
