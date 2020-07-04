
package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Volunteer {

    @SerializedName("idvolunteer")
    @Expose
    private Integer idvolunteer;
    @SerializedName("phoneNo")
    @Expose
    private String phoneNo;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("assignedtoFellow")
    @Expose
    private String assignedtoFellow;
    @SerializedName("assignedtoFellowContact")
    @Expose
    private String assignedtoFellowContact;
    @SerializedName("volunteercallList")
    @Expose
    private List<VolunteercallList> volunteercallList = null;
    @SerializedName("district")
    @Expose
    private String district;
    @SerializedName("block")
    @Expose
    private String block;
    @SerializedName("village")
    @Expose
    private Object village;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("address")
    @Expose
    private String address;

    public Integer getIdvolunteer() {
        return idvolunteer;
    }

    public void setIdvolunteer(Integer idvolunteer) {
        this.idvolunteer = idvolunteer;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAssignedtoFellow() {
        return assignedtoFellow;
    }

    public void setAssignedtoFellow(String assignedtoFellow) {
        this.assignedtoFellow = assignedtoFellow;
    }

    public String getAssignedtoFellowContact() {
        return assignedtoFellowContact;
    }

    public void setAssignedtoFellowContact(String assignedtoFellowContact) {
        this.assignedtoFellowContact = assignedtoFellowContact;
    }

    public List<VolunteercallList> getVolunteercallList() {
        return volunteercallList;
    }

    public void setVolunteercallList(List<VolunteercallList> volunteercallList) {
        this.volunteercallList = volunteercallList;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public Object getVillage() {
        return village;
    }

    public void setVillage(Object village) {
        this.village = village;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
