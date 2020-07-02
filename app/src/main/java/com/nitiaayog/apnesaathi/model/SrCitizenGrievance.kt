package com.nitiaayog.apnesaathi.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.nitiaayog.apnesaathi.adapter.GrievancesAdapter
import com.nitiaayog.apnesaathi.database.constants.DbConstants
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants

@Entity(tableName = DbConstants.Tables.TABLE_GRIEVANCES)
class SrCitizenGrievance {

    @PrimaryKey
    @ColumnInfo(name = DbConstants.Columns.Id, defaultValue = "-1")
    @SerializedName(ApiConstants.GrievanceId)
    var id: Int? = -1
        get() = field ?: -1
        set(@NonNull value) {
            field = value ?: -1
        }

    @ColumnInfo(name = DbConstants.Columns.VolunteerId, defaultValue = "-1")
    @SerializedName(ApiConstants.VolunteerId)
    var volunteerId: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.IsDiabetic, defaultValue = "-1")
    @SerializedName(ApiConstants.IsDiabetic)
    var diabetic: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.IsBloodPressure, defaultValue = "-1")
    @SerializedName(ApiConstants.IsBloodPressure)
    var bloodPressure: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.IsLungAilment, defaultValue = "-1")
    @SerializedName(ApiConstants.LungAilment)
    var lungAilment: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.IsCancerOrMajorSurgery, defaultValue = "-1")
    @SerializedName(ApiConstants.CancerOrMajorSurgery)
    var cancerOrMajorsurgery: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.OtherAilments, defaultValue = "-1")
    @SerializedName(ApiConstants.OtherAilments)
    var otherAilments: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.RemarkOnMedicalHistory, defaultValue = "-1")
    @SerializedName(ApiConstants.RemarkOnMedicalHistory)
    var remarksMedicalHistory: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.InfoTalkAbout, defaultValue = "-1")
    @SerializedName(ApiConstants.InfoTalkAbout)
    var relatedInfoTalkedAbout: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.HasCovidSymptoms, defaultValue = "-1")
    @SerializedName(ApiConstants.HasCovidSymptoms)
    var hasCovidSymptoms: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.HasCough, defaultValue = "-1")
    @SerializedName(ApiConstants.HasCough)
    var hasCough: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.HasFever, defaultValue = "-1")
    @SerializedName(ApiConstants.HasFever)
    var hasFever: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.HasShortnessOfBreath, defaultValue = "-1")
    @SerializedName(ApiConstants.HasShortnessOfBreath)
    var hasShortnessOfBreath: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.HasSoreThroat, defaultValue = "-1")
    @SerializedName(ApiConstants.HasSoreThroat)
    var hasSoreThroat: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.QuarantineStatus, defaultValue = "-1")
    @SerializedName(ApiConstants.QuarantineStatus)
    var quarantineStatus: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }
    @ColumnInfo(name = DbConstants.Columns.LackOfEssentialService, defaultValue = "No")
    @SerializedName(ApiConstants.LackOfEssentialService)
    var lackOfEssentialStatus: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.FoodShortage, defaultValue = "-1")
    @SerializedName(ApiConstants.FoodShortage)
    var foodShortage: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.MedicineShortage, defaultValue = "-1")
    @SerializedName(ApiConstants.MedicineShortage)
    var medicineShortage: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.AccessToBankingIssue, defaultValue = "-1")
    @SerializedName(ApiConstants.AccessToBankingIssue)
    var aceessToBankingIssue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.UtilitySupplyIssue, defaultValue = "-1")
    @SerializedName(ApiConstants.UtilityIssue)
    var utilitysupplyissue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.HygieneIssue, defaultValue = "-1")
    @SerializedName(ApiConstants.HygieneIssue)
    var hygieneissue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.SafetyIssue, defaultValue = "-1")
    @SerializedName(ApiConstants.SafetyIssue)
    var safetyissue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.EmergencyServiceIssue, defaultValue = "-1")
    @SerializedName(ApiConstants.EmergencyServiceIssue)
    var emergencyserviceissue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.PhoneAndInternetIssue, defaultValue = "-1")
    @SerializedName(ApiConstants.PhoneInternetIssue)
    var phoneandinternetissue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.IsEmergencyServiceRequired, defaultValue = "-1")
    @SerializedName(ApiConstants.IsEmergencyServicesRequired)
    var isemergencyservicerequired: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.RemarksImportantInfo, defaultValue = "-1")
    @SerializedName(ApiConstants.ImpRemarkInfo)
    var remakrsimportantinfo: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.LoggedDateTime, defaultValue = "-1")
    @SerializedName(ApiConstants.CreatedDate)
    var loggeddattime: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    var status: String = GrievancesAdapter.GRIEVANCE_PENDING

    /*var infoTalkedWith: String = related_info_talked_about ?: ""
    var grievanceId: String = idgrevance.toString() ?: "-1"
    var volunteerId: String = idvolunteer ?: ""
    var isDiabetic: String = diabetic ?: ""
    var isBloodpressure: String = bloodpressure ?: ""
    var isLungailment: String = lungailment ?: ""
    var cancerOrMajorSurgery: String = cancer_or_majorsurgery ?: ""
    var otherAilments: String = other_ailments ?: ""
    var remarksOnMedicalHistory: String = remarks_medical_history ?: ""
    var isCovidSymptoms: String = iscovidsymptoms ?: ""
    var hasCough: String = hascough ?: ""
    var hasFever: String = hasfever ?: ""
    var hasShortnessOfBreath: String = has_shortnes_of_breath ?: ""
    var hasSoreThroat: String = has_sorethroat ?: ""
    var quarantineStatus: String = quarantinestatus ?: ""
    var foodShortage: String = foodshortage ?: ""
    var medicineShortage: String = medicineshortage ?: ""
    var accessToBankingIssue: String = aceesstobankingissue ?: ""
    var utilitySupplyIssue: String = utilitysupplyissue ?: ""
    var hygieneIssue: String = hygieneissue ?: ""
    var safetyIssue: String = safetyissue ?: ""
    var emergencyServiceIssue: String = emergencyserviceissue ?: ""
    var phoneAndInternetIssue: String = phoneandinternetissue ?: ""
    var isEmergencyServiceRequired: String = isemergencyservicerequired ?: ""
    var remarksImpInfo: String = remakrsimportantinfo ?: ""*/
}