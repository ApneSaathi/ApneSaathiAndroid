package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Medicalandgreivance {
    @SerializedName("idgrevance")
    @Expose
    var idgrevance: Int? = null

    @SerializedName("callid")
    @Expose
    var callid: Int? = null

    @SerializedName("idvolunteer")
    @Expose
    var idvolunteer: Int? = null

    @SerializedName("diabetic")
    @Expose
    var diabetic: String? = null

    @SerializedName("bloodpressure")
    @Expose
    var bloodpressure: String? = null

    @SerializedName("lungailment")
    @Expose
    var lungailment: String? = null

    @SerializedName("cancer_or_majorsurgery")
    @Expose
    var cancerOrMajorsurgery: String? = null

    @SerializedName("other_ailments")
    @Expose
    var otherAilments: String? = null

    @SerializedName("remarks_medical_history")
    @Expose
    var remarksMedicalHistory: String? = null

    @SerializedName("related_info_talked_about")
    @Expose
    var relatedInfoTalkedAbout: String? = null

    @SerializedName("behavioural_change_noticed")
    @Expose
    var behaviouralChangeNoticed: String? = null

    @SerializedName("iscovidsymptoms")
    @Expose
    var iscovidsymptoms: String? = null

    @SerializedName("hascough")
    @Expose
    var hascough: String? = null

    @SerializedName("hasfever")
    @Expose
    var hasfever: String? = null

    @SerializedName("has_shortnes_of_breath")
    @Expose
    var hasShortnesOfBreath: String? = null

    @SerializedName("has_sorethroat")
    @Expose
    var hasSorethroat: String? = null

    @SerializedName("quarantinestatus")
    @Expose
    var quarantinestatus: Any? = null

    @SerializedName("lackofessentialservices")
    @Expose
    var lackofessentialservices: String? = null

    @SerializedName("foodshortage")
    @Expose
    var foodshortage: Int? = null

    @SerializedName("medicineshortage")
    @Expose
    var medicineshortage: Int? = null

    @SerializedName("aceesstobankingissue")
    @Expose
    var aceesstobankingissue: Int? = null

    @SerializedName("utilitysupplyissue")
    @Expose
    var utilitysupplyissue: Int? = null

    @SerializedName("hygieneissue")
    @Expose
    var hygieneissue: Int? = null

    @SerializedName("safetyissue")
    @Expose
    var safetyissue: Int? = null

    @SerializedName("emergencyserviceissue")
    @Expose
    var emergencyserviceissue: Int? = null

    @SerializedName("phoneandinternetissue")
    @Expose
    var phoneandinternetissue: Int? = null

    @SerializedName("isemergencyservicerequired")
    @Expose
    var isemergencyservicerequired: String? = null

    @SerializedName("remakrsimportantinfo")
    @Expose
    var remakrsimportantinfo: Any? = null

    @SerializedName("loggeddattime")
    @Expose
    var loggeddattime: String? = null

}