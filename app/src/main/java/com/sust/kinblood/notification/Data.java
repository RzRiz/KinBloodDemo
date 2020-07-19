package com.sust.kinblood.notification;

import com.google.android.gms.maps.model.LatLng;

public class Data {
    private String bloodGroup, donorHaveToGoLocationName, donorHaveToGoLocationAddress, condition, noOfBags, uid, fullName, phoneNumber;
    private LatLng donorHaveToGoLatLng;

    public Data() {
    }

    public Data(String bloodGroup, String donorHaveToGoLocationName, String condition, String noOfBags) {
        this.bloodGroup = bloodGroup;
        this.donorHaveToGoLocationName = donorHaveToGoLocationName;
        this.condition = condition;
        this.noOfBags = noOfBags;
    }

    public Data(String bloodGroup, String donorHaveToGoLocationName, String donorHaveToGoLocationAddress, String condition, String noOfBags, String uid, String fullName, String phoneNumber, LatLng donorHaveToGoLatLng) {
        this.bloodGroup = bloodGroup;
        this.donorHaveToGoLocationName = donorHaveToGoLocationName;
        this.donorHaveToGoLocationAddress = donorHaveToGoLocationAddress;
        this.condition = condition;
        this.noOfBags = noOfBags;
        this.uid = uid;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.donorHaveToGoLatLng = donorHaveToGoLatLng;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getDonorHaveToGoLocationName() {
        return donorHaveToGoLocationName;
    }

    public void setDonorHaveToGoLocationName(String donorHaveToGoLocationName) {
        this.donorHaveToGoLocationName = donorHaveToGoLocationName;
    }

    public String getDonorHaveToGoLocationAddress() {
        return donorHaveToGoLocationAddress;
    }

    public void setDonorHaveToGoLocationAddress(String donorHaveToGoLocationAddress) {
        this.donorHaveToGoLocationAddress = donorHaveToGoLocationAddress;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getNoOfBags() {
        return noOfBags;
    }

    public void setNoOfBags(String noOfBags) {
        this.noOfBags = noOfBags;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LatLng getDonorHaveToGoLatLng() {
        return donorHaveToGoLatLng;
    }

    public void setDonorHaveToGoLatLng(LatLng donorHaveToGoLatLng) {
        this.donorHaveToGoLatLng = donorHaveToGoLatLng;
    }
}
