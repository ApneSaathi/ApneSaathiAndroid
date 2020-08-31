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

/**
 * View model for handling all the actions related with grievances fragment
 * [dataManager] is used to store all the data that is required in the app.
 */
class GrievanceDetailsViewModel(private val dataManager: DataManager) : BaseViewModel() {

    companion object {

        @Synchronized
        fun getInstance(dataManager: DataManager): GrievanceDetailsViewModel = synchronized(this) {
            GrievanceDetailsViewModel(dataManager)
        }
    }
    /**
     * Method updating the grievance status.
     * [context] is the current activity context
     * [trackingId] is the id of the selected grievance that we need to send to the server.
     * [status] is the grievance status selected by the user
     * [description] is the description given by the user on updating the grievance
     * [grievanceType] is the priority type of grievance
     */
    internal fun updateGrievance(
        context: Context,
        trackingId: Int,
        status: String,
        description: String,
        grievanceType: String
    ) {
        if (checkNetworkAvailability(context, ApiProvider.ApiUpdateGrievanceDetails)) {
            val params = JsonObject()
            var updatedByParam = ""
            var statusGrievance = status
            if (status == "In Progress") {
                statusGrievance = "UNDER REVIEW"
                updatedByParam = ApiConstants.ReviewedBy
            }
            if (status == "Pending") {
                statusGrievance = "RAISED"
                updatedByParam = ApiConstants.RaisedBy
            }
            if (status == "Resolved") {
                updatedByParam = ApiConstants.ResolvedBy
            }
            params.addProperty(ApiConstants.GrievanceTrackingId, trackingId)
            params.addProperty(
                ApiConstants.GrievanceStatus,
                statusGrievance.toUpperCase(Locale.ROOT)
            )
            params.addProperty(ApiConstants.Description, description)
            params.addProperty(ApiConstants.GrievanceType, grievanceType)
            params.addProperty(updatedByParam, dataManager.getFirstName())
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

    /**
     * Method for fetching the data stream
     */
    fun getDataStream(): LiveData<NetworkRequestState> = loaderObservable
}