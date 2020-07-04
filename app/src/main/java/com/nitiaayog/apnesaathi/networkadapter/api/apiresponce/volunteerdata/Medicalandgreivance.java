
package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Medicalandgreivance {

    @SerializedName("idgrevance")
    @Expose
    private Integer idgrevance;
    @SerializedName("callid")
    @Expose
    private Integer callid;
    @SerializedName("idvolunteer")
    @Expose
    private Integer idvolunteer;
    @SerializedName("diabetic")
    @Expose
    private String diabetic;
    @SerializedName("bloodpressure")
    @Expose
    private String bloodpressure;
    @SerializedName("lungailment")
    @Expose
    private String lungailment;
    @SerializedName("cancer_or_majorsurgery")
    @Expose
    private String cancerOrMajorsurgery;
    @SerializedName("other_ailments")
    @Expose
    private String otherAilments;
    @SerializedName("remarks_medical_history")
    @Expose
    private String remarksMedicalHistory;
    @SerializedName("related_info_talked_about")
    @Expose
    private String relatedInfoTalkedAbout;
    @SerializedName("behavioural_change_noticed")
    @Expose
    private String behaviouralChangeNoticed;
    @SerializedName("iscovidsymptoms")
    @Expose
    private String iscovidsymptoms;
    @SerializedName("hascough")
    @Expose
    private String hascough;
    @SerializedName("hasfever")
    @Expose
    private String hasfever;
    @SerializedName("has_shortnes_of_breath")
    @Expose
    private String hasShortnesOfBreath;
    @SerializedName("has_sorethroat")
    @Expose
    private String hasSorethroat;
    @SerializedName("quarantinestatus")
    @Expose
    private Object quarantinestatus;
    @SerializedName("lackofessentialservices")
    @Expose
    private String lackofessentialservices;
    @SerializedName("foodshortage")
    @Expose
    private Integer foodshortage;
    @SerializedName("medicineshortage")
    @Expose
    private Integer medicineshortage;
    @SerializedName("aceesstobankingissue")
    @Expose
    private Integer aceesstobankingissue;
    @SerializedName("utilitysupplyissue")
    @Expose
    private Integer utilitysupplyissue;
    @SerializedName("hygieneissue")
    @Expose
    private Integer hygieneissue;
    @SerializedName("safetyissue")
    @Expose
    private Integer safetyissue;
    @SerializedName("emergencyserviceissue")
    @Expose
    private Integer emergencyserviceissue;
    @SerializedName("phoneandinternetissue")
    @Expose
    private Integer phoneandinternetissue;
    @SerializedName("isemergencyservicerequired")
    @Expose
    private String isemergencyservicerequired;
    @SerializedName("remakrsimportantinfo")
    @Expose
    private Object remakrsimportantinfo;
    @SerializedName("loggeddattime")
    @Expose
    private String loggeddattime;

    public Integer getIdgrevance() {
        return idgrevance;
    }

    public void setIdgrevance(Integer idgrevance) {
        this.idgrevance = idgrevance;
    }

    public Integer getCallid() {
        return callid;
    }

    public void setCallid(Integer callid) {
        this.callid = callid;
    }

    public Integer getIdvolunteer() {
        return idvolunteer;
    }

    public void setIdvolunteer(Integer idvolunteer) {
        this.idvolunteer = idvolunteer;
    }

    public String getDiabetic() {
        return diabetic;
    }

    public void setDiabetic(String diabetic) {
        this.diabetic = diabetic;
    }

    public String getBloodpressure() {
        return bloodpressure;
    }

    public void setBloodpressure(String bloodpressure) {
        this.bloodpressure = bloodpressure;
    }

    public String getLungailment() {
        return lungailment;
    }

    public void setLungailment(String lungailment) {
        this.lungailment = lungailment;
    }

    public String getCancerOrMajorsurgery() {
        return cancerOrMajorsurgery;
    }

    public void setCancerOrMajorsurgery(String cancerOrMajorsurgery) {
        this.cancerOrMajorsurgery = cancerOrMajorsurgery;
    }

    public String getOtherAilments() {
        return otherAilments;
    }

    public void setOtherAilments(String otherAilments) {
        this.otherAilments = otherAilments;
    }

    public String getRemarksMedicalHistory() {
        return remarksMedicalHistory;
    }

    public void setRemarksMedicalHistory(String remarksMedicalHistory) {
        this.remarksMedicalHistory = remarksMedicalHistory;
    }

    public String getRelatedInfoTalkedAbout() {
        return relatedInfoTalkedAbout;
    }

    public void setRelatedInfoTalkedAbout(String relatedInfoTalkedAbout) {
        this.relatedInfoTalkedAbout = relatedInfoTalkedAbout;
    }

    public String getBehaviouralChangeNoticed() {
        return behaviouralChangeNoticed;
    }

    public void setBehaviouralChangeNoticed(String behaviouralChangeNoticed) {
        this.behaviouralChangeNoticed = behaviouralChangeNoticed;
    }

    public String getIscovidsymptoms() {
        return iscovidsymptoms;
    }

    public void setIscovidsymptoms(String iscovidsymptoms) {
        this.iscovidsymptoms = iscovidsymptoms;
    }

    public String getHascough() {
        return hascough;
    }

    public void setHascough(String hascough) {
        this.hascough = hascough;
    }

    public String getHasfever() {
        return hasfever;
    }

    public void setHasfever(String hasfever) {
        this.hasfever = hasfever;
    }

    public String getHasShortnesOfBreath() {
        return hasShortnesOfBreath;
    }

    public void setHasShortnesOfBreath(String hasShortnesOfBreath) {
        this.hasShortnesOfBreath = hasShortnesOfBreath;
    }

    public String getHasSorethroat() {
        return hasSorethroat;
    }

    public void setHasSorethroat(String hasSorethroat) {
        this.hasSorethroat = hasSorethroat;
    }

    public Object getQuarantinestatus() {
        return quarantinestatus;
    }

    public void setQuarantinestatus(Object quarantinestatus) {
        this.quarantinestatus = quarantinestatus;
    }

    public String getLackofessentialservices() {
        return lackofessentialservices;
    }

    public void setLackofessentialservices(String lackofessentialservices) {
        this.lackofessentialservices = lackofessentialservices;
    }

    public Integer getFoodshortage() {
        return foodshortage;
    }

    public void setFoodshortage(Integer foodshortage) {
        this.foodshortage = foodshortage;
    }

    public Integer getMedicineshortage() {
        return medicineshortage;
    }

    public void setMedicineshortage(Integer medicineshortage) {
        this.medicineshortage = medicineshortage;
    }

    public Integer getAceesstobankingissue() {
        return aceesstobankingissue;
    }

    public void setAceesstobankingissue(Integer aceesstobankingissue) {
        this.aceesstobankingissue = aceesstobankingissue;
    }

    public Integer getUtilitysupplyissue() {
        return utilitysupplyissue;
    }

    public void setUtilitysupplyissue(Integer utilitysupplyissue) {
        this.utilitysupplyissue = utilitysupplyissue;
    }

    public Integer getHygieneissue() {
        return hygieneissue;
    }

    public void setHygieneissue(Integer hygieneissue) {
        this.hygieneissue = hygieneissue;
    }

    public Integer getSafetyissue() {
        return safetyissue;
    }

    public void setSafetyissue(Integer safetyissue) {
        this.safetyissue = safetyissue;
    }

    public Integer getEmergencyserviceissue() {
        return emergencyserviceissue;
    }

    public void setEmergencyserviceissue(Integer emergencyserviceissue) {
        this.emergencyserviceissue = emergencyserviceissue;
    }

    public Integer getPhoneandinternetissue() {
        return phoneandinternetissue;
    }

    public void setPhoneandinternetissue(Integer phoneandinternetissue) {
        this.phoneandinternetissue = phoneandinternetissue;
    }

    public String getIsemergencyservicerequired() {
        return isemergencyservicerequired;
    }

    public void setIsemergencyservicerequired(String isemergencyservicerequired) {
        this.isemergencyservicerequired = isemergencyservicerequired;
    }

    public Object getRemakrsimportantinfo() {
        return remakrsimportantinfo;
    }

    public void setRemakrsimportantinfo(Object remakrsimportantinfo) {
        this.remakrsimportantinfo = remakrsimportantinfo;
    }

    public String getLoggeddattime() {
        return loggeddattime;
    }

    public void setLoggeddattime(String loggeddattime) {
        this.loggeddattime = loggeddattime;
    }

}
