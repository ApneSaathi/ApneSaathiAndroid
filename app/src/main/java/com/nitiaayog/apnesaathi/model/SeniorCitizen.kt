package com.nitiaayog.apnesaathi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SeniorCitizen(
    @PrimaryKey
    @ColumnInfo(name = "user_id") val mUserId: Int,
    @ColumnInfo(name = "flag_") val mFlag: Boolean = false,
    @ColumnInfo(name = "flag_comment") val mFlagComment: String = "",
    @ColumnInfo(name = "status_") val mStatus: String = "",
    @ColumnInfo(name = "whom_did_talk_with") val mWhoDidTalkWith: String = "",
    @ColumnInfo(name = "medical_history") val mMedicalHistory: List<String>,
    @ColumnInfo(name = "related_info_talked_about") val mRelatedInfo: List<String>,
    @ColumnInfo(name = "behaviour_practices") val mBehaviourPractices: String = "",
    @ColumnInfo(name = "other_medical_problems") val mOtherMedicalProblems: String = "",
    @ColumnInfo(name = "covid_symptoms") val mCovidSymptoms: List<String>,
    @ColumnInfo(name = "non_covid_symptoms") val mNonCovidSymptoms: List<String>,
    @ColumnInfo(name = "quarantine_hospitalization_status") val mQuarantineOrHospitalizationStatus: String = "",
    @ColumnInfo(name = "is_there_lack_of_essential_service") val mIsThereLackOfEssentialService: Boolean = false,
    @ColumnInfo(name = "lack_of_essential_services") val mLackOfEssentials: List<String>,
    @ColumnInfo(name = "lack_of_essential_services_desc") val mLackOfEssentialsDescription: String = "",
    @ColumnInfo(name = "is_there_need_of_emergency_escalation") val mIsThereANeedOfEmergencyEscalation: Boolean = false,
    @ColumnInfo(name = "other_imp_info_desc") val mOtherImportantDescription: String = ""
)
