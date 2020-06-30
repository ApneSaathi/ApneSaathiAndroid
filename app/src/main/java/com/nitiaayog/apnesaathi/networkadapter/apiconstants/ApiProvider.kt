package com.nitiaayog.apnesaathi.networkadapter.apiconstants

import com.nitiaayog.apnesaathi.BuildConfig

object ApiProvider {

    private const val GET_LOGIN_USER = "Volunteer/LoginVolunteer"
    const val ApiLoginUser = BuildConfig.HOST_URL + GET_LOGIN_USER

    private const val GET_SAVE_SR_CITIZEN_FEED_BACK_FORM = "Volunteer/SaveSeniorCitizenFeedbackForm"
    const val ApiSaveSeniorCitizenFeedbackForm =
        BuildConfig.HOST_URL + GET_SAVE_SR_CITIZEN_FEED_BACK_FORM

    private const val GET_REGISTER_SR_CITIZEN = "Volunteer/NewSrCitizenRegistration"
    const val ApiRegisterSeniorCitizen = BuildConfig.HOST_URL + GET_REGISTER_SR_CITIZEN

    private const val GET_SR_CITIZEN_DETAILS = "Volunteer/seniorcitizenDetails"
    const val ApiSeniorCitizenDetails = BuildConfig.HOST_URL + GET_SR_CITIZEN_DETAILS
}