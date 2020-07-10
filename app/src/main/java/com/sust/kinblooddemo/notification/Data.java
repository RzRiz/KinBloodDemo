package com.sust.kinblooddemo.notification;

public class Data {
    private String bloodGroup, hospital, condition, noOfBags, uid;

    public Data() {
    }

    public Data(String bloodGroup, String hospital, String condition, String noOfBags, String uid) {
        this.bloodGroup = bloodGroup;
        this.hospital = hospital;
        this.condition = condition;
        this.noOfBags = noOfBags;
        this.uid = uid;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
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
}
