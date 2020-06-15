package com.sust.kinblooddemo;

public class DonationInfoHelper {
    private int dDay, dMonth, dYear, donate_times;
    private String donatedBefore;

    public DonationInfoHelper() {
    }

    public DonationInfoHelper(int dDay, int dMonth, int dYear, int donate_times, String donatedBefore) {
        this.dDay = dDay;
        this.dMonth = dMonth;
        this.dYear = dYear;
        this.donate_times = donate_times;
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

    public int getDonate_times() {
        return donate_times;
    }

    public void setDonate_times(int donate_times) {
        this.donate_times = donate_times;
    }

    public String getDonatedBefore() {
        return donatedBefore;
    }

    public void setDonatedBefore(String donatedBefore) {
        this.donatedBefore = donatedBefore;
    }
}
