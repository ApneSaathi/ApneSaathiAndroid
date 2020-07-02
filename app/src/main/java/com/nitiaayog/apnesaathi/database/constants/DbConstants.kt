package com.nitiaayog.apnesaathi.database.constants

object DbConstants {

    object Tables {
        const val TABLE_CALL_DETAILS: String = "call_details"
        const val TABLE_GRIEVANCES: String = "grievances"
    }

    object Columns {
        const val Id: String = "id"
        const val VolunteerId: String = "volunteer_id"
        const val EmailId: String = "email_id"
        const val Name: String = "name"
        const val ContactNumber: String = "contact_number"
        const val Age: String = "age"
        const val Gender: String = "gender"
        const val Address: String = "address"
        const val State: String = "state"
        const val District: String = "district"
        const val Block: String = "block"
        const val CallStatus: String = "call_status"
        const val CallSubStatus: String = "call_sub_status"
        const val TalkedWith: String = "talked_with"
        const val Remark: String = "remark"
        const val IsDiabetic: String = "is_diabetic"
        const val IsBloodPressure: String = "is_blood_pressure"
        const val IsLungAilment: String = "is_lung_ailment"
        const val IsCancerOrMajorSurgery: String = "cancer_or_major_surgery"
        const val OtherAilments: String = "other_ailments"
        const val RemarkOnMedicalHistory: String = "remarks_on_medical_history"
        const val InfoTalkAbout: String = "info_talked_about"
        const val HasCovidSymptoms: String = "has_covid_symptoms"
        const val HasCough: String = "has_cough"
        const val HasFever: String = "has_fever"
        const val HasShortnessOfBreath: String = "has_shortness_of_breath"
        const val HasSoreThroat: String = "has_sore_throat"
        const val QuarantineStatus: String = "quarantine_status"
        const val LackOfEssentialService: String = "lack_of_essential_service"
        const val FoodShortage: String = "food_shortage"
        const val MedicineShortage: String = "medicine_shortage"
        const val AccessToBankingIssue: String = "access_to_banking_issue"
        const val UtilitySupplyIssue: String = "utility_supply_issue"
        const val HygieneIssue: String = "hygiene_issue"
        const val SafetyIssue: String = "safety_issue"
        const val EmergencyServiceIssue: String = "emergency_service_issue"
        const val PhoneAndInternetIssue: String = "phone_and_internet_issue"
        const val IsEmergencyServiceRequired: String = "is_emergency_service_required"
        const val RemarksImportantInfo: String = "remarks_important_info"
        const val LoggedDateTime: String = "logged_date_time"
    }
}