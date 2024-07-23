package de.fhe.ai.colivingpilot.network

interface NetworkResult<T> {
    fun onSuccess(data: T?)
    fun onFailure(code: String?)
}

interface NetworkResultNoData {
    fun onSuccess()
    fun onFailure(code: String?)
}