
package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VolunteerDataResponse {

    @SerializedName("statusCode")
    @Expose
    private String statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("volunteerId")
    @Expose
    private Object volunteerId;
    @SerializedName("idgrevance")
    @Expose
    private Object idgrevance;
    @SerializedName("volunteer")
    @Expose
    private Volunteer volunteer;
    @SerializedName("volunteerassignment")
    @Expose
    private Object volunteerassignment;
    @SerializedName("medicalandgreivance")
    @Expose
    private Object medicalandgreivance;
    @SerializedName("loginOTP")
    @Expose
    private Object loginOTP;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Object volunteerId) {
        this.volunteerId = volunteerId;
    }

    public Object getIdgrevance() {
        return idgrevance;
    }

    public void setIdgrevance(Object idgrevance) {
        this.idgrevance = idgrevance;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Object getVolunteerassignment() {
        return volunteerassignment;
    }

    public void setVolunteerassignment(Object volunteerassignment) {
        this.volunteerassignment = volunteerassignment;
    }

    public Object getMedicalandgreivance() {
        return medicalandgreivance;
    }

    public void setMedicalandgreivance(Object medicalandgreivance) {
        this.medicalandgreivance = medicalandgreivance;
    }

    public Object getLoginOTP() {
        return loginOTP;
    }

    public void setLoginOTP(Object loginOTP) {
        this.loginOTP = loginOTP;
    }

}
