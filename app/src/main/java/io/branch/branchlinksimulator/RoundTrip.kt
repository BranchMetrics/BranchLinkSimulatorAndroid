package io.branch.branchlinksimulator

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

@Serializable
data class RoundTrip(
    val url: String,
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String = LocalDateTime.now().format(formatter),
    val request: BranchRequest? = null,
    var response: BranchResponse? = null
)

@Serializable
data class BranchRequest(
    val body: String?
)

@Serializable
data class BranchResponse(
    val statusCode: String,
    val body: String
)