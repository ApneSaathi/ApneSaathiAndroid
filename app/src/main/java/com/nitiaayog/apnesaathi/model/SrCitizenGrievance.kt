package com.nitiaayog.apnesaathi.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.adapter.GrievancesAdapter
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.database.converters.DateTimeConverter
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

@Entity(tableName = Tables.TABLE_GRIEVANCES)
open class SrCitizenGrievance {

    @PrimaryKey
    @ColumnInfo(name = Columns.Id, defaultValue = "-1")
    @SerializedName(ApiConstants.GrievanceId)
    var id: Int? = -1
        get() = field ?: -1
        set(@NonNull value) {
            field = value ?: -1
        }

    @ColumnInfo(name = Columns.CallId, defaultValue = "-1")
    @SerializedName(ApiConstants.CallId)
    var callId: Int? = -1
        get() = field ?: -1
        set(@NonNull value) {
            field = value ?: -1
        }

    @ColumnInfo(name = Columns.Name, defaultValue = "")
    var srCitizenName: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.Gender, defaultValue = "")
    var gender: String? = ""
        get() = field ?: ""
        set(value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.VolunteerId, defaultValue = "-1")
    @SerializedName(ApiConstants.VolunteerId)
    var volunteerId: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.IsDiabetic, defaultValue = "-1")
    @SerializedName(ApiConstants.IsDiabetic)
    var hasDiabetic: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.IsBloodPressure, defaultValue = "-1")
    @SerializedName(ApiConstants.IsBloodPressure)
    var hasBloodPressure: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.IsLungAilment, defaultValue = "-1")
    @SerializedName(ApiConstants.LungAilment)
    var hasLungAilment: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.IsCancerOrMajorSurgery, defaultValue = "-1")
    @SerializedName(ApiConstants.CancerOrMajorSurgery)
    var cancerOrMajorSurgery: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.OtherAilments, defaultValue = "-1")
    @SerializedName(ApiConstants.OtherAilments)
    var otherAilments: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.RemarkOnMedicalHistory, defaultValue = "-1")
    @SerializedName(ApiConstants.RemarkOnMedicalHistory)
    var remarksMedicalHistory: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.InfoTalkAbout, defaultValue = "-1")
    @SerializedName(ApiConstants.InfoTalkAbout)
    var relatedInfoTalkedAbout: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.NoticedBehaviouralChanges, defaultValue = "-1")
    @SerializedName(ApiConstants.NoticedBehaviouralChange)
    var behavioralChangesNoticed: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.HasCovidSymptoms, defaultValue = "-1")
    @SerializedName(ApiConstants.HasCovidSymptoms)
    var hasCovidSymptoms: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.HasCough, defaultValue = "-1")
    @SerializedName(ApiConstants.HasCough)
    var hasCough: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.HasFever, defaultValue = "-1")
    @SerializedName(ApiConstants.HasFever)
    var hasFever: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.HasShortnessOfBreath, defaultValue = "-1")
    @SerializedName(ApiConstants.HasShortnessOfBreath)
    var hasShortnessOfBreath: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.HasSoreThroat, defaultValue = "-1")
    @SerializedName(ApiConstants.HasSoreThroat)
    var hasSoreThroat: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.QuarantineStatus, defaultValue = "-1")
    @SerializedName(ApiConstants.QuarantineStatus)
    var quarantineStatus: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.LackOfEssentialServices, defaultValue = "-1")
    @SerializedName(ApiConstants.LackOfEssentialServices)
    var lackOfEssentialServices: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.Description, defaultValue = "")
    @SerializedName(ApiConstants.Description)
    var description: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.FoodShortage, defaultValue = "-1")
    @SerializedName(ApiConstants.FoodShortage)
    var foodShortage: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.MedicineShortage, defaultValue = "-1")
    @SerializedName(ApiConstants.MedicineShortage)
    var medicineShortage: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.AccessToBankingIssue, defaultValue = "-1")
    @SerializedName(ApiConstants.AccessToBankingIssue)
    var accessToBankingIssue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.UtilitySupplyIssue, defaultValue = "-1")
    @SerializedName(ApiConstants.UtilityIssue)
    var utilitySupplyIssue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.HygieneIssue, defaultValue = "-1")
    @SerializedName(ApiConstants.HygieneIssue)
    var hygieneIssue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.SafetyIssue, defaultValue = "-1")
    @SerializedName(ApiConstants.SafetyIssue)
    var safetyIssue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.EmergencyServiceIssue, defaultValue = "-1")
    @SerializedName(ApiConstants.EmergencyServiceIssue)
    var emergencyServiceIssue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.PhoneAndInternetIssue, defaultValue = "-1")
    @SerializedName(ApiConstants.PhoneInternetIssue)
    var phoneAndInternetIssue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.IsEmergencyServiceRequired, defaultValue = "-1")
    @SerializedName(ApiConstants.IsEmergencyServicesRequired)
    var emergencyServiceRequired: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = Columns.RemarksImportantInfo, defaultValue = "-1")
    @SerializedName(ApiConstants.ImpRemarkInfo)
    var impRemarkInfo: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @TypeConverters(DateTimeConverter::class)
    @ColumnInfo(name = Columns.CreatedDate, defaultValue = "-1")
    @SerializedName(ApiConstants.CreatedDate)
    var createdDate: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    var status: String = GrievancesAdapter.GRIEVANCE_RAISED

    fun createCopy(): SrCitizenGrievance {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, SrCitizenGrievance::class.java)
    }
}