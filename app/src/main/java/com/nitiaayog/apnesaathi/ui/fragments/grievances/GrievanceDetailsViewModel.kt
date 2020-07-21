package com.nitiaayog.apnesaathi.ui.fragments.grievances

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseViewModel
import java.util.*

class GrievanceDetailsViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): GrievanceDetailsViewModel = synchronized(this) {
            GrievanceDetailsViewModel(dataManager)
        }
    }

    internal fun updateGrievance(
        context: Context,
        trackingId: Int,
        status: String,
        description: String,
        grievanceType: String
    ) {
        if (checkNetworkAvailability(context, ApiProvider.ApiUpdateGrievanceDetails)) {
            val params = JsonObject()
            var statusGrievance = status
            if(status == "In Progress"){
                statusGrievance = "UNDER REVIEW"
            }
            if(status=="Pending"){
                statusGrievance = "RAISED"
            }
            params.addProperty(ApiConstants.GrievanceTrackingId, trackingId)
            params.addProperty(ApiConstants.GrievanceStatus, statusGrievance.toUpperCase(Locale.ROOT))
            params.addProperty(ApiConstants.Description, description)
            params.addProperty(ApiConstants.GrievanceType, grievanceType)
            dataManager.updateGrievanceDetails(params).doOnSubscribe {
                loaderObservable.value =
                    NetworkRequestState.LoadingData(ApiProvider.ApiUpdateGrievanceDetails)
            }.subscribe({
                if (it.status == "0")
                    loaderObservable.value = NetworkRequestState.SuccessResponse(
                        ApiProvider.ApiUpdateGrievanceDetails, it
                    )
                else loaderObservable.value =
                    NetworkRequestState.Error(ApiProvider.ApiUpdateGrievanceDetails)
            }, {
                loaderObservable.value =
                    NetworkRequestState.ErrorResponse(ApiProvider.ApiUpdateGrievanceDetails, it)
            }).autoDispose(disposables)
        }
    }
    fun getDataStream(): LiveData<NetworkRequestState> = loaderObservable
}