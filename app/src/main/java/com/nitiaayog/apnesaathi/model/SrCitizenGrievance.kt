package com.nitiaayog.apnesaathi.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nitiaayog.apnesaathi.database.constants.DbConstants

@Entity(tableName = DbConstants.Tables.TABLE_GRIEVANCES)
class SrCitizenGrievance {

    @PrimaryKey
    @ColumnInfo(name = DbConstants.Columns.Id, defaultValue = "-1")
    var idgrevance: Int? = -1
        get() = field ?: -1
        set(@NonNull value) {
            field = value ?: -1
        }

    @ColumnInfo(name = DbConstants.Columns.VolunteerId, defaultValue = "-1")
    var idvolunteer: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.IsDiabetic, defaultValue = "-1")
    var diabetic: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.IsBloodPressure, defaultValue = "-1")
    var bloodpressure: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.IsLungAilment, defaultValue = "-1")
    var lungailment: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.IsCancerOrMajorSurgery, defaultValue = "-1")
    var cancer_or_majorsurgery: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.OtherAilments, defaultValue = "-1")
    var other_ailments: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.RemarkOnMedicalHistory, defaultValue = "-1")
    var remarks_medical_history: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.InfoTalkAbout, defaultValue = "-1")
    var related_info_talked_about: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.HasCovidSymptoms, defaultValue = "-1")
    var iscovidsymptoms: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.HasCough, defaultValue = "-1")
    var hascough: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.HasFever, defaultValue = "-1")
    var hasfever: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.HasShortnessOfBreath, defaultValue = "-1")
    var has_shortnes_of_breath: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.HasSoreThroat, defaultValue = "-1")
    var has_sorethroat: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.QuarantineStatus, defaultValue = "-1")
    var quarantinestatus: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.FoodShortage, defaultValue = "-1")
    var foodshortage: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.MedicineShortage, defaultValue = "-1")
    var medicineshortage: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.AccessToBankingIssue, defaultValue = "-1")
    var aceesstobankingissue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.UtilitySupplyIssue, defaultValue = "-1")
    var utilitysupplyissue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.HygieneIssue, defaultValue = "-1")
    var hygieneissue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.SafetyIssue, defaultValue = "-1")
    var safetyissue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.EmergencyServiceIssue, defaultValue = "-1")
    var emergencyserviceissue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.PhoneAndInternetIssue, defaultValue = "-1")
    var phoneandinternetissue: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.IsEmergencyServiceRequired, defaultValue = "-1")
    var isemergencyservicerequired: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.RemarksImportantInfo, defaultValue = "-1")
    var remakrsimportantinfo: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

    @ColumnInfo(name = DbConstants.Columns.LoggedDateTime, defaultValue = "-1")
    var loggeddattime: String? = ""
        get() = field ?: ""
        set(@NonNull value) {
            field = value ?: ""
        }

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