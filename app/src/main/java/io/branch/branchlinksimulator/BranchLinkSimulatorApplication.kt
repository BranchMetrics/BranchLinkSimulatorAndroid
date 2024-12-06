package io.branch.branchlinksimulator

import android.app.Application
import android.content.Context
import io.branch.referral.Branch
import java.util.UUID

class BranchLinkSimulatorApplication: Application() {
    lateinit var currentConfig: ApiConfiguration
    lateinit var roundTripStore: RoundTripStore
        private set

    override fun onCreate() {
        super.onCreate()

        val preferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        currentConfig = ApiConfigManager.loadConfigOrDefault(preferences)

        Branch.setAPIUrl(currentConfig.apiUrl)

        roundTripStore = RoundTripStore(this)
        Branch.enableLogging(roundTripStore)
        // Branch object initialization
        Branch.getAutoInstance(this, currentConfig.branchKey)

        // Retrieve or create the bls_session_id
        val sharedPreferences = getSharedPreferences("branch_session_prefs", Context.MODE_PRIVATE)
        val blsSessionId = sharedPreferences.getString("bls_session_id", null) ?: run {
            val newId = UUID.randomUUID().toString()
            sharedPreferences.edit().putString("bls_session_id", newId).apply()
            newId
        }

        // Set the bls_session_id in Branch request metadata
        Branch.getInstance().setRequestMetadata("bls_session_id", blsSessionId)
    }
}