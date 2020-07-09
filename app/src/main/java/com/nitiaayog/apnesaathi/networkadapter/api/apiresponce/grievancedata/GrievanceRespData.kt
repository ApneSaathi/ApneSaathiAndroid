package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.grievancedata

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.model.GrievanceData
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

class GrievanceRespData {

    @SerializedName(ApiConstants.StatusCode)
    @Expose
    private var statusCode: String? = null

    @SerializedName(ApiConstants.Message)
    @Expose
    private var message: String? = null

    @SerializedName(ApiConstants.VolunteerId)
    @Expose
    private var volunteerId: String? = null

    @SerializedName(ApiConstants.Volunteer)
    @Expose
    private var volunteer: Any? = null

    @SerializedName(ApiConstants.VolunteerAssignment)
    @Expose
    private var volunteerassignment: Any? = null

    @SerializedName(ApiConstants.MedicalGrievances)
    @Expose
    private var medicalandgreivance: Any? = null

    @SerializedName(ApiConstants.GrievanceTracking)
    @Expose
    private val grievanceTrackingList: List<GrievanceData> = mutableListOf()

    @SerializedName(ApiConstants.LoginOtp)
    @Expose
    private var loginOTP: Any? = null

    fun getTrackingList():List<GrievanceData> = grievanceTrackingList
    fun getStatus():String = statusCode?:""
}