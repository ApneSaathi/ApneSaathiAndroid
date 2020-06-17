package com.nitiaayog.apnesaathi.model

import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

class SeniorCitizen(

    @SerializedName(ApiConstants.UserId)
    private val sNo: Int,

    @SerializedName(ApiConstants.UserId)
    private val mDisplayName: String = "",

    @SerializedName(ApiConstants.UserId)
    private val mPhoneNumber: Long,

    @SerializedName(ApiConstants.UserId)
    private val mEmail: String = "",

    @SerializedName(ApiConstants.UserId)
    private val mFlag: Boolean = false,

    @SerializedName(ApiConstants.UserId)
    private val mFlagComment: String = "",

    @SerializedName(ApiConstants.UserId)
    private val mGender: String = "",

    @SerializedName(ApiConstants.UserId)
    private val mState: String = "",

    @SerializedName(ApiConstants.UserId)
    private val mDistrict: String = "",

    @SerializedName(ApiConstants.UserId)
    private val mBlock: String = "",

    @SerializedName(ApiConstants.UserId)
    private val mVillage: String = "",

    @SerializedName(ApiConstants.UserId)
    private val mCreatedBy: String = "",

    @SerializedName(ApiConstants.UserId)
    private val mAssignTo: String = "",

    @SerializedName(ApiConstants.UserId)
    private val mContentDescription: String = "",

    @SerializedName(ApiConstants.UserId)
    private val mStatus: String = ""
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
) {
    val userId: Int = sNo
    val displayName: String = mDisplayName
    val phoneNumber: Long = mPhoneNumber
    val email: String = mEmail
    val flag: Boolean = mFlag
    val flagComment: String = mFlagComment
    val gender: String = mGender
    val state: String = mState
    val district: String = mDistrict
    val block: String = mBlock
    val village: String = mVillage
    val createdBy: String = mCreatedBy
    val assignTo: String = mAssignTo
    val contentDescription: String = mContentDescription
    val status: String = mStatus
}