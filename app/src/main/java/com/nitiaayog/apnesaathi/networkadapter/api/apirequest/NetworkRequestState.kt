package com.nitiaayog.apnesaathi.networkadapter.api.apirequest

sealed class NetworkRequestState {
    object NetworkNotAvailable : NetworkRequestState()
    object LoadingData : NetworkRequestState()
    class ErrorResponse(val status: Int, val throwable: Throwable? = null) : NetworkRequestState()
    class SuccessResponse<T>(val data: T?) : NetworkRequestState()
}