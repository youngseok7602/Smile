package com.example.mju.smile;

/**
 * Created by Jeong on 2017-11-18.
 */


//주소록 정보(폰번호, 이름)에 대한 클래스
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
