package com.sust.kinblooddemo.notification;

public class Data {
    private String bloodGroup, hospital, condition, noOfBags;

    public Data() {
    }

    public Data(String bloodGroup, String hospital, String condition, String noOfBags) {
        this.bloodGroup = bloodGroup;
        this.hospital = hospital;
        this.condition = condition;
        this.noOfBags = noOfBags;
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
}
