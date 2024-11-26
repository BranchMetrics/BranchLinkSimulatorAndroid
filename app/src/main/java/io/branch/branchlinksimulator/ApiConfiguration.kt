package io.branch.branchlinksimulator

data class ApiConfiguration(
    val branchKey: String,
    val apiUrl: String,
    val appId: String,
    val staging: Boolean
)

const val STAGING = "Staging"
const val PRODUCTION = "Production"
const val STAGING_AC = "Staging AC"
const val PRODUCTION_AC = "Production AC"

val apiConfigurationsMap: Map<String, ApiConfiguration> = mapOf(
    STAGING_AC to ApiConfiguration(
        branchKey = "key_live_juoZrlpzQZvBQbwR33GO5hicszlTGnVT",
        apiUrl = "https://protected-api.stage.branch.io/",
        appId = "1387589751543976586",
        staging = true
    ),
    STAGING to ApiConfiguration(
        branchKey = "key_live_plqOidX7fW71Gzt0LdCThkemDEjCbTgx",
        apiUrl = "https://api.stage.branch.io/",
        appId = "436637608899006753",
        staging = true
    ),
    PRODUCTION_AC to ApiConfiguration(
        branchKey = "key_live_hshD4wiPK2sSxfkZqkH30ggmyBfmGmD7",
        apiUrl = "https://protected-api.branch.io/",
        appId = "1284289243903971463",
        staging = false
    ),
    PRODUCTION to ApiConfiguration(
        branchKey = "key_live_iDiV7ZewvDm9GIYxUnwdFdmmvrc9m3Aw",
        apiUrl = "https://api2.branch.io/",
        appId = "1364964166783226677",
        staging = false
    )
)
