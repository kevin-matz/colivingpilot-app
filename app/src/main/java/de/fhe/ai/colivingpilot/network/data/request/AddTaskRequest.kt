package de.fhe.ai.colivingpilot.network.data.request

data class AddTaskRequest(
    val title: String,
    val description: String,
    val beerbonus: Int
)
