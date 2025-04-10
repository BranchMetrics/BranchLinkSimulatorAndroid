package io.branch.branchlinksimulator

data class ApiConfiguration(
    val branchKey: String,
    val apiUrl: String,
    val appId: String,
    val staging: Boolean
)

const val STAGING = "[Stage] External Services"
const val PRODUCTION = "Pro Production"
const val STAGING_AC = "[Stage] Adv. Compliance Sandbox"
const val PRODUCTION_AC = "Adv. Compliance Sandbox"
const val STAGING_LS = "[Stage] LS + ENGMT Ess. Demo"
const val PRODUCTION_LS = "LS + ENGMT Ess. Demo"

val apiConfigurationsMap: Map<String, ApiConfiguration> = mapOf(
    STAGING_LS to ApiConfiguration(
        branchKey = "key_live_nFc30jPoTV53LhvHat5XXffntufA4O0l",
        apiUrl = "https://api.stage.branch.io/",
        appId = "1425582272655938028",
        staging = true
    ),
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
    PRODUCTION_LS to ApiConfiguration(
        branchKey = "key_live_hsdXYiNiH9pfDv50xrFt0gbgEEiMIqFO",
        apiUrl = "https://api2.branch.io/",
        appId = "1425583205569811094",
        staging = false
    ),
    PRODUCTION_AC to ApiConfiguration(
        branchKey = "key_live_hshD4wiPK2sSxfkZqkH30ggmyBfmGmD7",
        apiUrl = "https://protected-api.branch.io/",
        appId = "1284289243903971463",
        staging = false
    ),
    PRODUCTION to ApiConfiguration(
        branchKey = "key_live_iCh53eMdH5aIibeOqRYBojgpyrmU4gd8",
        apiUrl = "https://api2.branch.io/",
        appId = "1425585005551178435",
        staging = false
    )
)
