package com.nitiaayog.apnesaathi.networkadapter.api.apirequest

sealed class NetworkRequestState {
    object NetworkNotAvailable : NetworkRequestState()
    object LoadingData : NetworkRequestState()
    object Error : NetworkRequestState()
    class ErrorResponse(val status: Int, val throwable: Throwable? = null) : NetworkRequestState()
    class SuccessResponse<T>(val data: T?) : NetworkRequestState()
}