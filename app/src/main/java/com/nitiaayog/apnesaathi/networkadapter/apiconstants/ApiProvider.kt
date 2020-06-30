package com.nitiaayog.apnesaathi.networkadapter.apiconstants

import com.nitiaayog.apnesaathi.BuildConfig

object ApiProvider {

    private const val GET_LOGIN_USER: String = "Volunteer/LoginVolunteer"
    const val ApiLoginUser: String = BuildConfig.HOST_URL + GET_LOGIN_USER

    private const val GET_LOAD_DASHBOARD: String = "Volunteer/loadDashboard"
    const val ApiLoadDashboard: String = BuildConfig.HOST_URL + GET_LOAD_DASHBOARD

    private const val GET_SAVE_SR_CITIZEN_FEED_BACK_FORM: String =
        "Volunteer/SaveSeniorCitizenFeedbackForm"
    const val ApiSaveSeniorCitizenFeedbackForm: String =
        BuildConfig.HOST_URL + GET_SAVE_SR_CITIZEN_FEED_BACK_FORM

    private const val GET_REGISTER_SR_CITIZEN: String = "Volunteer/registerNewSrCitizen"
    const val ApiRegisterSeniorCitizen: String = BuildConfig.HOST_URL + GET_REGISTER_SR_CITIZEN

    private const val GET_SR_CITIZEN_DETAILS = "Volunteer/seniorcitizenDetails"
    const val ApiSeniorCitizenDetails = BuildConfig.HOST_URL + GET_SR_CITIZEN_DETAILS
}