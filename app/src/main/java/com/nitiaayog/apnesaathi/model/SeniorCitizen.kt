package com.nitiaayog.apnesaathi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

@Entity
class SeniorCitizen(
    @PrimaryKey(autoGenerate = true) val sNo: Int,
    @ColumnInfo(name = "display_name") val mDisplayName: String = "",
    @ColumnInfo(name = "phone_number") val mPhoneNumber: Long,
    @ColumnInfo(name = "email_") val mEmail: String = "",
    @ColumnInfo(name = "flag_") val mFlag: Boolean = false,
    @ColumnInfo(name = "flag_comment") val mFlagComment: String = "",
    @ColumnInfo(name = "gender_") val mGender: String = "",
    @ColumnInfo(name = "state_") val mState: String = "",
    @ColumnInfo(name = "district_") val mDistrict: String = "",
    @ColumnInfo(name = "block_") val mBlock: String = "",
    @ColumnInfo(name = "village_") val mVillage: String = "",
    @ColumnInfo(name = "status_") val mStatus: String = "",
    @ColumnInfo(name = "covid_symptoms") val mCovidSymptoms:String="",
    @ColumnInfo(name = "non_covid_symptoms") val mNonCovidSymptoms:String=""
//    private val mCreatedBy: String = "",
//    private val mAssignTo: String = "",
//    private val mContentDescription: String = "",

//    private val mDateOfOnBoarding:String="",
//    private val mProcess:String="",
//    private val mUpdatedDate:String="",
//    private val mDisposition: String="",
//    private val mAddress:String="",
//    private val mRemark:String="",
//    private val mEmotionalStatus:String="",
//    private val mGeographicalAreaCode:String="",
//    private val mFirstTimeCalls:String="",
//    private val mContentDeliveryCall:String="",
//    private val mFollowUpCalls:String="",
//    private val mCitizenInformation:String="",
//    private val mDisemminatedModule:String="",
//    private val mMediumOfDisemmination:String="",
//    private val mIssueResolved:String="",
//    private val mResourceShared:String="",
//    private val mChannelOfDisemmination:String="",
//    private val mBehaviorChangePractice:String="",
//    private val mCovidSymptoms:String="",
//    private val mNonCovidSymptoms:String="",
//    private val mComplaintFoodShortage:String="",
//    private val mComplaintMedicineShortage:String="",
//    private val mUnavailabilityOfBankingServices:String="",
//    private val mComplaintsOfBasicUtilities:String="",
//    private val mComplaintsOfUnhygenicConditions:String="",
//    private val mComplaintsOfLackOfInformation:String=""
)