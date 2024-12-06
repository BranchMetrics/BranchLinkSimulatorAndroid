package io.branch.branchlinksimulator

import android.content.SharedPreferences

object ApiConfigManager {
    fun loadConfigOrDefault(preferences: SharedPreferences): ApiConfiguration {
        return loadConfig(preferences) ?: apiConfigurationsMap[PRODUCTION] ?: ApiConfiguration("N/A", "N/A", "N/A", false)
    }

    private fun loadConfig(preferences: SharedPreferences): ApiConfiguration? {
        val configName = preferences.getString(SELECTED_CONFIG_NAME, null)
        return configName?.let { apiConfigurationsMap[it] }
    }
}
