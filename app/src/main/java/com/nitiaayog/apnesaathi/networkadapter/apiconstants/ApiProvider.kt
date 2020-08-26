package com.nitiaayog.apnesaathi.networkadapter.apiconstants

import com.nitiaayog.apnesaathi.BuildConfig

object ApiProvider {
    const val ApiLoginUser: String = BuildConfig.ApiHost + BuildConfig.GetLoginUser
    const val ApiVerifyPassword: String = BuildConfig.ApiHost + BuildConfig.VerifyPassword
    const val ApiGetVolunteers: String = BuildConfig.ApiHost + BuildConfig.GetVolunteers
    const val ApiGetVolunteerData: String = BuildConfig.ApiHost + BuildConfig.GetVolunteerData
    const val ApiLoadDashboard: String = BuildConfig.ApiHost + BuildConfig.LoadDashboard
    const val ApiGrievanceTracking: String = BuildConfig.ApiHost + BuildConfig.GetGrievanceTracking
    const val ApiSaveSeniorCitizenFeedbackForm: String =
        BuildConfig.ApiHost + BuildConfig.SaveSrCitizenFeedbackForm
    const val ApiRegisterSeniorCitizen: String = BuildConfig.ApiHost + BuildConfig.RegisterSrCitizen
    const val ApiSeniorCitizenDetails = BuildConfig.ApiHost + BuildConfig.GetSeniorCitizensDetails
    const val ApiUpdateGrievanceDetails = BuildConfig.ApiHost + BuildConfig.UpdateGrievanceDetails
    const val ApiUpdateProfile = BuildConfig.ApiHost + BuildConfig.UpdateProfile
    const val ApiRateVolunteer = BuildConfig.ApiHost + BuildConfig.RateVolunteer
    const val ApiEmergencyContact = BuildConfig.ApiHost + BuildConfig.getEmergencyContactDetails
}