package com.example.jeong.test_firebase;

/**
 * Created by Jeong on 2017-11-18.
 */

public class Contacts {

    private String phoneNumber;
    private String name;

    public Contacts(String phoneNumber, String name){

        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
