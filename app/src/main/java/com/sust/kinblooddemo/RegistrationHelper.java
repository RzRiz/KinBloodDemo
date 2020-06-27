package com.sust.kinblooddemo;

public class RegistrationHelper {
    private String address, gender, bloodGroup, occupation, institute, donarstatus, donatedBefore;
    private int bDay, bMonth, bYear, donateTimes, dDay, dMonth, dYear;

    public RegistrationHelper(){}


    public RegistrationHelper(String address, String gender, String bloodGroup, String occupation, String institute, String donarstatus, int bDay, int bMonth, int bYear, int donateTimes, int dDay, int dMonth, int dYear, String donatedBefore) {
        this.address = address;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.occupation = occupation;
        this.institute = institute;
        this.donarstatus = donarstatus;
        this.bDay = bDay;
        this.bMonth = bMonth;
        this.bYear = bYear;
        this.donateTimes = donateTimes;
        this.dDay = dDay;
        this.dMonth = dMonth;
        this.dYear = dYear;
        this.donatedBefore = donatedBefore;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getDonarstatus() {
        return donarstatus;
    }

    public void setDonarstatus(String donarstatus) {
        this.donarstatus = donarstatus;
    }

    public int getbDay() {
        return bDay;
    }

    public void setbDay(int bDay) {
        this.bDay = bDay;
    }

    public int getbMonth() {
        return bMonth;
    }

    public void setbMonth(int bMonth) {
        this.bMonth = bMonth;
    }

    public int getbYear() {
        return bYear;
    }

    public void setbYear(int bYear) {
        this.bYear = bYear;
    }

    public int getDonateTimes() {
        return donateTimes;
    }

    public void setDonateTimes(int donateTimes) {
        this.donateTimes = donateTimes;
    }

    public int getdDay() {
        return dDay;
    }

    public void setdDay(int dDay) {
        this.dDay = dDay;
    }

    public int getdMonth() {
        return dMonth;
    }

    public void setdMonth(int dMonth) {
        this.dMonth = dMonth;
    }

    public int getdYear() {
        return dYear;
    }

    public void setdYear(int dYear) {
        this.dYear = dYear;
    }

    public String getDonatedBefore() {
        return donatedBefore;
    }

    public void setDonatedBefore(String donatedBefore) {
        this.donatedBefore = donatedBefore;
    }
}
