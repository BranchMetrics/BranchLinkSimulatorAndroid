package io.branch.branchlinksimulator

import android.app.Application
import android.content.Context
import android.util.Log
import io.branch.referral.Branch
import kotlinx.coroutines.CompletableDeferred
import java.util.UUID

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BranchLinkSimulatorApplication: Application() {
    lateinit var currentConfig: ApiConfiguration
    lateinit var roundTripStore: RoundTripStore
        private set

    private val applicationJob = SupervisorJob()
    val applicationScope = CoroutineScope(Dispatchers.Main + applicationJob)
    val branchInitializationSignal = CompletableDeferred<Unit>()

    override fun onCreate() {
        super.onCreate()

        val preferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)
        currentConfig = ApiConfigManager.loadConfigOrDefault(preferences)

        Branch.setAPIUrl(currentConfig.apiUrl)

        roundTripStore = RoundTripStore(this)
        Branch.enableLogging(roundTripStore)

        // Retrieve or create the bls_session_id
        val sharedPreferences = getSharedPreferences("branch_session_prefs", Context.MODE_PRIVATE)
        val blsSessionId = sharedPreferences.getString("bls_session_id", null) ?: run {
            val newId = UUID.randomUUID().toString()
            sharedPreferences.edit().putString("bls_session_id", newId).apply()
            newId
        }
        applicationScope.launch {
            // Coroutine used to move creating the Branch singleton through Branch.getAutoInstance() to background thread
            setupBranchInstance(this@BranchLinkSimulatorApplication, currentConfig.branchKey)
            withContext(Dispatchers.Main) {
                // Set the bls_session_id in Branch request metadata
                Branch.getInstance().setRequestMetadata("bls_session_id", blsSessionId)
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationJob.cancel()
    }

    suspend fun setupBranchInstance(context: Context, branchKey: String) {
        withContext(Dispatchers.IO) {
            // Branch object initialization
            Branch.getAutoInstance(context, branchKey)
        }
        branchInitializationSignal.complete(Unit)
    }
}