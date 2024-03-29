package com.nitiaayog.apnesaathi.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nitiaayog.apnesaathi.database.constants.Columns
import com.nitiaayog.apnesaathi.database.constants.Tables
import com.nitiaayog.apnesaathi.model.SrCitizenGrievance

/**
 *
 * */
@Dao
interface GrievancesDao {

    /**
     *
     * */
    @Transaction
    @Query("SELECT * FROM ${Tables.TABLE_GRIEVANCES}")
    fun getGrievances(): LiveData<MutableList<SrCitizenGrievance>>

    @Query("SELECT * FROM ${Tables.TABLE_GRIEVANCES} WHERE ${Columns.CallId}=:callId ORDER BY ${Columns.CreatedDate}")
    fun getAllUniqueGrievances(callId: Int = -1): LiveData<MutableList<SrCitizenGrievance>>

    @Query("SELECT * FROM ${Tables.TABLE_GRIEVANCES} LIMIT :dataCount")
    fun getFewGrievances(dataCount: Int = 3): LiveData<MutableList<SrCitizenGrievance>>

    @Query("SELECT * FROM ${Tables.TABLE_GRIEVANCES} WHERE (${Columns.CallId}=:callId) AND (${Columns.CreatedDate} BETWEEN :start AND :end) ORDER BY ${Columns.CreatedDate} DESC LIMIT 1")
    fun getGrievance(callId: Int, start: String, end: String): SrCitizenGrievance?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(grievances: List<SrCitizenGrievance>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(grievances: SrCitizenGrievance): Long

    /**
     * Delete Previous Issue data
     * */
    @Query("DELETE FROM ${Tables.TABLE_GRIEVANCES} WHERE ${Columns.DeleteIDAfterSync} =1")
    fun deletePreviousData()

    /**
     * Make a check whether issue exist or not
     * */
    @Query("SELECT * FROM ${Tables.TABLE_GRIEVANCES} WHERE ${Columns.Id}=:id AND ${Columns.CallId}=:callId")
    fun isDataExist(id: Int, callId: Int): SrCitizenGrievance?

    /**
     * Delete Issue if not required
     * */
    @Delete
    fun deleteGrievance(grievances: SrCitizenGrievance)

    /**
     * Update an issue details
     * */
    @Query(
        "UPDATE ${Tables.TABLE_GRIEVANCES} SET ${Columns.IsDiabetic}=:hasDiabetic,${Columns.IsBloodPressure}=:hasBloodPressure,${Columns.IsLungAilment}=:hasLungAilment,${Columns.IsCancerOrMajorSurgery}=:cancerOrMajorSurgery,${Columns.OtherAilments}=:otherAilments,${Columns.RemarkOnMedicalHistory}=:remarksMedicalHistory,${Columns.InfoTalkAbout}=:relatedInfoTalkedAbout,${Columns.NoticedBehaviouralChanges}=:behavioralChangesNoticed,${Columns.HasCovidSymptoms}=:hasCovidSymptoms,${Columns.HasCough}=:hasCough,${Columns.HasFever}=:hasFever,${Columns.HasShortnessOfBreath}=:hasShortnessOfBreath,${Columns.HasSoreThroat}=:hasSoreThroat,${Columns.QuarantineStatus}=:quarantineStatus,${Columns.LackOfEssentialServices}=:lackOfEssentialServices,${Columns.FoodShortage}=:foodShortage,${Columns.MedicineShortage}=:medicineShortage,${Columns.AccessToBankingIssue}=:accessToBankingIssue,${Columns.UtilitySupplyIssue}=:utilitySupplyIssue,${Columns.HygieneIssue}=:hygieneIssue,${Columns.SafetyIssue}=:safetyIssue,${Columns.EmergencyServiceIssue}=:emergencyServiceIssue,${Columns.PhoneAndInternetIssue}=:phoneAndInternetIssue,${Columns.IsEmergencyServiceRequired}=:emergencyServiceRequired,${Columns.RemarksImportantInfo}=:impRemarkInfo WHERE ${Columns.Id}=:id AND ${Columns.CallId}=:callId"
    )
    fun update(
        id: Int, callId: Int, hasDiabetic: String, hasBloodPressure: String, hasLungAilment: String,
        cancerOrMajorSurgery: String, otherAilments: String, remarksMedicalHistory: String,
        relatedInfoTalkedAbout: String, behavioralChangesNoticed: String, hasCovidSymptoms: String,
        hasCough: String, hasFever: String, hasShortnessOfBreath: String, hasSoreThroat: String,
        quarantineStatus: String, lackOfEssentialServices: String, foodShortage: String,
        medicineShortage: String, accessToBankingIssue: String, utilitySupplyIssue: String,
        hygieneIssue: String, safetyIssue: String, emergencyServiceIssue: String,
        phoneAndInternetIssue: String, emergencyServiceRequired: String, impRemarkInfo: String
    )

    /**
     * If data of issue is already available in database then just update else insert it
     * */
    @Transaction
    fun insertOrUpdate(grievances: List<SrCitizenGrievance>) = grievances.forEach {
        if (insert(it) == -1L)
            update(
                it.id!!, it.callId!!, it.hasDiabetic!!, it.hasBloodPressure!!, it.hasLungAilment!!,
                it.cancerOrMajorSurgery!!, it.otherAilments!!, it.remarksMedicalHistory!!,
                it.relatedInfoTalkedAbout!!, it.behavioralChangesNoticed!!, it.hasCovidSymptoms!!,
                it.hasCough!!, it.hasFever!!, it.hasShortnessOfBreath!!, it.hasSoreThroat!!,
                it.quarantineStatus!!, it.lackOfEssentialServices!!, it.foodShortage!!,
                it.medicineShortage!!, it.accessToBankingIssue!!, it.utilitySupplyIssue!!,
                it.hygieneIssue!!, it.safetyIssue!!, it.emergencyServiceIssue!!,
                it.phoneAndInternetIssue!!, it.emergencyServiceIssue!!, it.impRemarkInfo!!
            )
    }
}