package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.loginresponse

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResponse {

    @SerializedName("statusCode")
    @Expose
    private var statusCode: String? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    private var id: String? = null

    @SerializedName("volunteer")
    @Expose
    private var volunteer: Any? = null

    @SerializedName("volunteerassignment")
    @Expose
    private var volunteerassignment: Any? = null

    @SerializedName("medicalandgreivance")
    @Expose
    private var medicalandgreivance: Any? = null

    @SerializedName("loginOTP")
    @Expose
    private var loginOTP: Any? = null

    private var role: String? = ""

    fun getStatusCode(): String? {
        return statusCode
    }

    fun setStatusCode(statusCode: String?) {
        this.statusCode = statusCode
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getVolunteer(): Any? {
        return volunteer
    }

    fun setVolunteer(volunteer: Any?) {
        this.volunteer = volunteer
    }

    fun getVolunteerassignment(): Any? {
        return volunteerassignment
    }

    fun setVolunteerassignment(volunteerassignment: Any?) {
        this.volunteerassignment = volunteerassignment
    }

    fun getMedicalandgreivance(): Any? {
        return medicalandgreivance
    }

    fun setMedicalandgreivance(medicalandgreivance: Any?) {
        this.medicalandgreivance = medicalandgreivance
    }

    fun getLoginOTP(): Any? {
        return loginOTP
    }

    fun setLoginOTP(loginOTP: Any?) {
        this.loginOTP = loginOTP
    }

    fun getRole(): String = role ?: ""

    fun setRole(role: String) {
        this.role = role
    }
}