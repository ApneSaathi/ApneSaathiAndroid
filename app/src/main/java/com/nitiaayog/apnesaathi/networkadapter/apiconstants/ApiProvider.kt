package com.nitiaayog.apnesaathi.networkadapter.apiconstants

import com.nitiaayog.apnesaathi.BuildConfig

object ApiProvider {

    private const val GET_LOGIN_USER: String = "Volunteer/LoginVolunteer"
    const val ApiLoginUser: String = BuildConfig.HOST_URL + GET_LOGIN_USER

    private const val GET_VOLUNTEER_DATA: String = "Volunteer/VolunteerData"
    const val Api_volunteer_Data: String = BuildConfig.HOST_URL + GET_VOLUNTEER_DATA

    private const val GET_LOAD_DASHBOARD: String = "Volunteer/loadDashboard"
    const val ApiLoadDashboard: String = BuildConfig.HOST_URL + GET_LOAD_DASHBOARD

    private const val GET_GRIEVANCE_TRACKING: String = "Volunteer/getGreivanceDetails"
    const val ApiGrievanceTracking: String = BuildConfig.HOST_URL + GET_GRIEVANCE_TRACKING

    private const val GET_SAVE_SR_CITIZEN_FEED_BACK_FORM: String = "Volunteer/saveForm"
    const val ApiSaveSeniorCitizenFeedbackForm: String =
        BuildConfig.HOST_URL + GET_SAVE_SR_CITIZEN_FEED_BACK_FORM

    private const val GET_REGISTER_SR_CITIZEN: String = "Volunteer/registerNewSrCitizen"
    const val ApiRegisterSeniorCitizen: String = BuildConfig.HOST_URL + GET_REGISTER_SR_CITIZEN

    private const val GET_SR_CITIZEN_DETAILS = "Volunteer/seniorcitizenDetails"
    const val ApiSeniorCitizenDetails = BuildConfig.HOST_URL + GET_SR_CITIZEN_DETAILS

    private const val GET_UPDATE_GRIEVANCE_DETAILS = "Volunteer/updateGreivanceDetails"
    const val ApiUpdateGrievanceDetails = BuildConfig.HOST_URL + GET_UPDATE_GRIEVANCE_DETAILS

}