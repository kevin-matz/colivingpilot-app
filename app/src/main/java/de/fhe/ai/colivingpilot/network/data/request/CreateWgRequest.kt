package de.fhe.ai.colivingpilot.network.data.request

data class CreateWgRequest(
    val name: String,
    val maximumMembers: Int
)