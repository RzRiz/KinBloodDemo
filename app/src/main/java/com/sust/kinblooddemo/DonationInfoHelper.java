package com.sust.kinblooddemo;

public class DonationInfoHelper {
    private int dDay, dMonth, dYear, donateTimes;
    private String donatedBefore;

    public DonationInfoHelper() {
    }

    public DonationInfoHelper(int dDay, int dMonth, int dYear, int donateTimes, String donatedBefore) {
        this.dDay = dDay;
        this.dMonth = dMonth;
        this.dYear = dYear;
        this.donateTimes = donateTimes;
        this.donatedBefore = donatedBefore;
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

    public int getDonateTimes() {
        return donateTimes;
    }

    public void setDonateTimes(int donateTimes) {
        this.donateTimes = donateTimes;
    }

    public String getDonatedBefore() {
        return donatedBefore;
    }

    public void setDonatedBefore(String donatedBefore) {
        this.donatedBefore = donatedBefore;
    }
}
