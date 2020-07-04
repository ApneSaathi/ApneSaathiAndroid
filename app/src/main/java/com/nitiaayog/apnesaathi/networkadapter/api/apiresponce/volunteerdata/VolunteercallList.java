
package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VolunteercallList {

    @SerializedName("callid")
    @Expose
    private Integer callid;
    @SerializedName("idvolunteer")
    @Expose
    private Integer idvolunteer;
    @SerializedName("namesrcitizen")
    @Expose
    private String namesrcitizen;
    @SerializedName("phonenosrcitizen")
    @Expose
    private String phonenosrcitizen;
    @SerializedName("agesrcitizen")
    @Expose
    private Integer agesrcitizen;
    @SerializedName("gendersrcitizen")
    @Expose
    private String gendersrcitizen;
    @SerializedName("addresssrcitizen")
    @Expose
    private String addresssrcitizen;
    @SerializedName("emailsrcitizen")
    @Expose
    private String emailsrcitizen;
    @SerializedName("statesrcitizen")
    @Expose
    private String statesrcitizen;
    @SerializedName("districtsrcitizen")
    @Expose
    private String districtsrcitizen;
    @SerializedName("blocknamesrcitizen")
    @Expose
    private String blocknamesrcitizen;
    @SerializedName("villagesrcitizen")
    @Expose
    private String villagesrcitizen;
    @SerializedName("callstatusCode")
    @Expose
    private Integer callstatusCode;
    @SerializedName("callstatussubCode")
    @Expose
    private Integer callstatussubCode;
    @SerializedName("talkedwith")
    @Expose
    private String talkedwith;
    @SerializedName("assignedbyMember")
    @Expose
    private Object assignedbyMember;
    @SerializedName("remarks")
    @Expose
    private Object remarks;
    @SerializedName("loggeddateTime")
    @Expose
    private String loggeddateTime;
    @SerializedName("medicalandgreivance")
    @Expose
    private List<Medicalandgreivance> medicalandgreivance = null;

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

    public String getNamesrcitizen() {
        return namesrcitizen;
    }

    public void setNamesrcitizen(String namesrcitizen) {
        this.namesrcitizen = namesrcitizen;
    }

    public String getPhonenosrcitizen() {
        return phonenosrcitizen;
    }

    public void setPhonenosrcitizen(String phonenosrcitizen) {
        this.phonenosrcitizen = phonenosrcitizen;
    }

    public Integer getAgesrcitizen() {
        return agesrcitizen;
    }

    public void setAgesrcitizen(Integer agesrcitizen) {
        this.agesrcitizen = agesrcitizen;
    }

    public String getGendersrcitizen() {
        return gendersrcitizen;
    }

    public void setGendersrcitizen(String gendersrcitizen) {
        this.gendersrcitizen = gendersrcitizen;
    }

    public String getAddresssrcitizen() {
        return addresssrcitizen;
    }

    public void setAddresssrcitizen(String addresssrcitizen) {
        this.addresssrcitizen = addresssrcitizen;
    }

    public String getEmailsrcitizen() {
        return emailsrcitizen;
    }

    public void setEmailsrcitizen(String emailsrcitizen) {
        this.emailsrcitizen = emailsrcitizen;
    }

    public String getStatesrcitizen() {
        return statesrcitizen;
    }

    public void setStatesrcitizen(String statesrcitizen) {
        this.statesrcitizen = statesrcitizen;
    }

    public String getDistrictsrcitizen() {
        return districtsrcitizen;
    }

    public void setDistrictsrcitizen(String districtsrcitizen) {
        this.districtsrcitizen = districtsrcitizen;
    }

    public String getBlocknamesrcitizen() {
        return blocknamesrcitizen;
    }

    public void setBlocknamesrcitizen(String blocknamesrcitizen) {
        this.blocknamesrcitizen = blocknamesrcitizen;
    }

    public String getVillagesrcitizen() {
        return villagesrcitizen;
    }

    public void setVillagesrcitizen(String villagesrcitizen) {
        this.villagesrcitizen = villagesrcitizen;
    }

    public Integer getCallstatusCode() {
        return callstatusCode;
    }

    public void setCallstatusCode(Integer callstatusCode) {
        this.callstatusCode = callstatusCode;
    }

    public Integer getCallstatussubCode() {
        return callstatussubCode;
    }

    public void setCallstatussubCode(Integer callstatussubCode) {
        this.callstatussubCode = callstatussubCode;
    }

    public String getTalkedwith() {
        return talkedwith;
    }

    public void setTalkedwith(String talkedwith) {
        this.talkedwith = talkedwith;
    }

    public Object getAssignedbyMember() {
        return assignedbyMember;
    }

    public void setAssignedbyMember(Object assignedbyMember) {
        this.assignedbyMember = assignedbyMember;
    }

    public Object getRemarks() {
        return remarks;
    }

    public void setRemarks(Object remarks) {
        this.remarks = remarks;
    }

    public String getLoggeddateTime() {
        return loggeddateTime;
    }

    public void setLoggeddateTime(String loggeddateTime) {
        this.loggeddateTime = loggeddateTime;
    }

    public List<Medicalandgreivance> getMedicalandgreivance() {
        return medicalandgreivance;
    }

    public void setMedicalandgreivance(List<Medicalandgreivance> medicalandgreivance) {
        this.medicalandgreivance = medicalandgreivance;
    }

}
