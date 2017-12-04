package com.example.mju.smile;

/**
 * Created by Jeong on 2017-11-15.
 */

public class UserData {
    private String phoneNumber;
    private String token;

    public UserData(){
    }

    public UserData(String phoneNumber, String token) {
        this.phoneNumber = phoneNumber;
        this.token = token;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
