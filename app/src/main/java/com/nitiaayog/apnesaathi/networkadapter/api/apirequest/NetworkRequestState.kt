package com.nitiaayog.apnesaathi.networkadapter.api.apirequest

sealed class NetworkRequestState {
    object NetworkNotAvailable : NetworkRequestState()
    object LoadingData : NetworkRequestState()
    class ErrorResponse(val message: Throwable?) : NetworkRequestState()
    class SuccessResponse<T>(val data: T?) : NetworkRequestState()
}