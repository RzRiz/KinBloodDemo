package com.sust.kinblooddemo;

public class BirthDateHelper {
    private int bDay, bMonth, bYear;

    public BirthDateHelper() {
    }

    public BirthDateHelper(int bDay, int bMonth, int bYear) {
        this.bDay = bDay;
        this.bMonth = bMonth;
        this.bYear = bYear;
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
}
