package io.branch.branchlinksimulator

import android.content.Context
import android.content.SharedPreferences
import io.branch.interfaces.IBranchLoggingCallbacks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.*
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RoundTripStore(context: Context) : IBranchLoggingCallbacks {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val storageKey = "savedRoundTrips"
    private val preferences: SharedPreferences =
        context.getSharedPreferences("RoundTripStorePrefs", Context.MODE_PRIVATE)

    private val _roundTrips = MutableStateFlow(loadRoundTrips())
    val roundTrips: StateFlow<List<RoundTrip>> = _roundTrips

    private val FAILED = "failed to parse"

    private val json = Json { prettyPrint = true }

    override fun onBranchLog(logMessage: String?, severityConstantName: String?) {
        scope.launch(Dispatchers.Main) {
            processLog(logMessage)
        }
    }

    private suspend fun addRoundTrip(url: String) {
        withContext(Dispatchers.Main) {
            val newTrip = RoundTrip(
                url = url,
            )
            _roundTrips.value = listOf(newTrip) + _roundTrips.value
            trimToLimit()
            saveRoundTrips()
        }
    }

    private suspend fun addRequest(request: BranchRequest) {
        withContext(Dispatchers.Main) {
            val trips = _roundTrips.value.toMutableList()
            _roundTrips.value[0].let { currentTrip ->
                val updatedTrip = currentTrip.copy(request = request)
                trips[0] = updatedTrip
                _roundTrips.value = trips
                saveRoundTrips()
            }
        }
    }

    private suspend fun addResponse(response: BranchResponse) {
        withContext(Dispatchers.Main) {
            val trips = _roundTrips.value.toMutableList()
            _roundTrips.value[0].let { currentTrip ->
                val updatedTrip = currentTrip.copy(response = response)
                trips[0] = updatedTrip
                _roundTrips.value = trips
                saveRoundTrips()
            }
        }
    }

    private suspend fun processLog(log: String?) {
        if (log == null) return
        println(log)
        when {
            log.contains("posting to") -> {
                addRoundTrip(parseUrl(log) ?: FAILED)
            }
            log.contains("Post value =") -> {
                val request = parseRequestLog(log)
                addRequest(request)
            }
            log.contains("Server returned") -> {
                val response = parseResponseLog(log)
                addResponse(response)
            }
        }
    }

    private fun parseUrl(log: String): String? {
        val regex = "(?<=posting to\\s)(https?://\\S+)".toRegex()
        return regex.find(log)?.value
    }

    private fun String.prettyPrint(): String {
        val jsonElement = json.parseToJsonElement(this)
        return json.encodeToString(jsonElement)
    }

    private fun parseBody(log: String, identifier: String): String? {
        val bodyStart = log.indexOf(identifier).takeIf { it != -1 }?.let { it + identifier.length }
        return bodyStart?.let {
            log.substring(it).trim().prettyPrint()
        }
    }

    private fun parseRequestLog(log: String): BranchRequest {
        return BranchRequest(
            body = parseBody(log, "Post value = ") ?: FAILED
        )
    }

    private fun parseResponseLog(log: String): BranchResponse {
        val statusCode = "(?<=Status: \\[)\\d+(?=])".toRegex().find(log)?.value
        return BranchResponse(
            statusCode = statusCode ?: FAILED,
            body = parseBody(log, "Data: ") ?: FAILED
        )
    }


    private fun trimToLimit() {
        if (_roundTrips.value.size > 30) {
            _roundTrips.value = _roundTrips.value.take(30)
        }
    }

    private fun saveRoundTrips() {
        try {
            val data = Json.encodeToString(_roundTrips.value)
            preferences.edit().putString(storageKey, data).apply()
        } catch (e: Exception) {
            println("Failed to save round trips: ${e.message}")
        }
    }

    private fun loadRoundTrips(): List<RoundTrip> {
        return try {
            val data = preferences.getString(storageKey, null)
            if (data != null) Json.decodeFromString(data) else emptyList()
        } catch (e: Exception) {
            println("Failed to load round trips: ${e.message}")
            emptyList()
        }
    }
}