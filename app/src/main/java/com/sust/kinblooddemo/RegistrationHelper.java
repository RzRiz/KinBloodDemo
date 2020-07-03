package com.sust.kinblooddemo;

public class RegistrationHelper {
    private String currentAddress, homeDistrict, gender, bloodGroup, occupation, institute, donarStatus;
    private int bDay, bMonth, bYear, donateTimes, dDay, dMonth, dYear;

    public RegistrationHelper(){}


    public RegistrationHelper(String currentAddress, String homeDistrict, String gender, String bloodGroup, String occupation, String institute, String donarStatus, int bDay, int bMonth, int bYear, int donateTimes, int dDay, int dMonth, int dYear) {
        this.currentAddress = currentAddress;
        this.homeDistrict = homeDistrict;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.occupation = occupation;
        this.institute = institute;
        this.donarStatus = donarStatus;
        this.bDay = bDay;
        this.bMonth = bMonth;
        this.bYear = bYear;
        this.donateTimes = donateTimes;
        this.dDay = dDay;
        this.dMonth = dMonth;
        this.dYear = dYear;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getHomeDistrict() {
        return homeDistrict;
    }

    public void setHomeDistrict(String homeDistrict) {
        this.homeDistrict = homeDistrict;
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

    public String getDonarStatus() {
        return donarStatus;
    }

    public void setDonarStatus(String donarStatus) {
        this.donarStatus = donarStatus;
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
}
