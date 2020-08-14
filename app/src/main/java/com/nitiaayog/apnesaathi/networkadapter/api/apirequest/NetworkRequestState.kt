package com.nitiaayog.apnesaathi.networkadapter.api.apirequest

sealed class NetworkRequestState {
    class NetworkNotAvailable(val apiName: String) : NetworkRequestState()
    class LoadingData(val apiName: String) : NetworkRequestState()
    class Error(val apiName: String) : NetworkRequestState()
    class ErrorResponse(val apiName: String,val throwable: Throwable? = null,val errorCode:Int =-1) :
        NetworkRequestState()
    class SuccessResponse<T>(val apiName: String, val data: T?) : NetworkRequestState()
}