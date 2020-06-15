package com.sust.kinblooddemo;

public class SignupHelper {
    private String fullName, phoneNumber, email, password, donarstatus;

    public SignupHelper() {
    }

    public SignupHelper(String fullName, String phoneNumber, String email, String password, String donarstatus) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.donarstatus = donarstatus;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDonarstatus() {
        return donarstatus;
    }

    public void setDonarstatus(String donarstatus) {
        this.donarstatus = donarstatus;
    }
}
