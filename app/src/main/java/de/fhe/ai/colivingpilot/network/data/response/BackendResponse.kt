package de.fhe.ai.colivingpilot.network.data.response

/**
 * Template for responses coming from our backend
 */
data class BackendResponse<T>(
    val status: String,
    val data: T
)

data class BackendResponseNoData(
    val status: String
)