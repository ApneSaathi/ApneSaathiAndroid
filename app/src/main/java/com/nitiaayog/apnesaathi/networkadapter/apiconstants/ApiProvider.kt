package com.nitiaayog.apnesaathi.networkadapter.apiconstants

import com.nitiaayog.apnesaathi.BuildConfig

object ApiProvider {

    private const val BASE_URL: String = BuildConfig.DEV_HOST
    //private const val PROD_HOST: String = BuildConfig.PROD_HOST

    //private val DEV_HOST = if(BuildConfig.DEBUG) DEV_HOST else PROD_HOST

    private const val GET_LOGIN_USER: String = "Volunteer/loginVolunteerOrAdmin"
    const val ApiLoginUser: String = BASE_URL + GET_LOGIN_USER

    private const val VERIFY_PASSWORD: String = "Volunteer/verifyPassword"
    const val ApiVerifyPassword: String = BASE_URL + VERIFY_PASSWORD

    private const val GET_VOLUNTEERS: String = "Volunteer/volunteersList"
    const val ApiGetVolunteers: String = BASE_URL + GET_VOLUNTEERS

    private const val GET_VOLUNTEER_DATA: String = "Volunteer/VolunteerData"
    const val Api_volunteer_Data: String = BASE_URL + GET_VOLUNTEER_DATA

    private const val GET_LOAD_DASHBOARD: String = "Volunteer/loadDashboard"
    const val ApiLoadDashboard: String = BASE_URL + GET_LOAD_DASHBOARD

    private const val GET_GRIEVANCE_TRACKING: String = "Volunteer/getGreivanceDetails"
    const val ApiGrievanceTracking: String = BASE_URL + GET_GRIEVANCE_TRACKING

    private const val GET_SAVE_SR_CITIZEN_FEED_BACK_FORM: String = "Volunteer/saveForm"
    const val ApiSaveSeniorCitizenFeedbackForm: String =
        BASE_URL + GET_SAVE_SR_CITIZEN_FEED_BACK_FORM

    private const val GET_REGISTER_SR_CITIZEN: String = "Volunteer/registerNewSrCitizen"
    const val ApiRegisterSeniorCitizen: String = BASE_URL + GET_REGISTER_SR_CITIZEN

    private const val GET_SR_CITIZEN_DETAILS = "Volunteer/seniorcitizenDetails"
    const val ApiSeniorCitizenDetails = BASE_URL + GET_SR_CITIZEN_DETAILS

    private const val GET_UPDATE_GRIEVANCE_DETAILS = "Volunteer/updateGreivanceDetails"
    const val ApiUpdateGrievanceDetails = BASE_URL + GET_UPDATE_GRIEVANCE_DETAILS

    private const val GET_SAVINGPROFILE = "Volunteer/updateProfile"
    const val Api_UPDATEPROFILE = BASE_URL + GET_SAVINGPROFILE
}