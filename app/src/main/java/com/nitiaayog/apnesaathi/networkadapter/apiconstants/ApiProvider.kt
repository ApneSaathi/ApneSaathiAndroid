package com.nitiaayog.apnesaathi.networkadapter.apiconstants

import com.nitiaayog.apnesaathi.BuildConfig

object ApiProvider {

    private const val DEV_HOST: String = BuildConfig.DEV_HOST
    //private const val PROD_HOST: String = BuildConfig.PROD_HOST

    //private val DEV_HOST = if(BuildConfig.DEBUG) DEV_HOST else PROD_HOST

    private const val GET_LOGIN_USER: String = "Volunteer/LoginVolunteer"
    const val ApiLoginUser: String = DEV_HOST + GET_LOGIN_USER

    private const val GET_VOLUNTEER_DATA: String = "Volunteer/VolunteerData"
    const val Api_volunteer_Data: String = DEV_HOST + GET_VOLUNTEER_DATA

    private const val GET_LOAD_DASHBOARD: String = "Volunteer/loadDashboard"
    const val ApiLoadDashboard: String = DEV_HOST + GET_LOAD_DASHBOARD

    private const val GET_GRIEVANCE_TRACKING: String = "Volunteer/getGreivanceDetails"
    const val ApiGrievanceTracking: String = DEV_HOST + GET_GRIEVANCE_TRACKING

    private const val GET_SAVE_SR_CITIZEN_FEED_BACK_FORM: String = "Volunteer/saveForm"
    const val ApiSaveSeniorCitizenFeedbackForm: String =
        DEV_HOST + GET_SAVE_SR_CITIZEN_FEED_BACK_FORM

    private const val GET_REGISTER_SR_CITIZEN: String = "Volunteer/registerNewSrCitizen"
    const val ApiRegisterSeniorCitizen: String = DEV_HOST + GET_REGISTER_SR_CITIZEN

    private const val GET_SR_CITIZEN_DETAILS = "Volunteer/seniorcitizenDetails"
    const val ApiSeniorCitizenDetails = DEV_HOST + GET_SR_CITIZEN_DETAILS

    private const val GET_UPDATE_GRIEVANCE_DETAILS = "Volunteer/updateGreivanceDetails"
    const val ApiUpdateGrievanceDetails = DEV_HOST + GET_UPDATE_GRIEVANCE_DETAILS

}