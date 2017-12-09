package com.example.mju.smile;

import java.io.Serializable;

/**
 * Created by Jeong on 2017-11-25.
 */

//유저정보(이름, 폰번호, 토큰) 클래스
public class AllUserData implements Serializable{

    private String name;
    private String phoneNumber;
    private String token;

    public AllUserData() {
    }

    public AllUserData(String name, String phoneNumber, String token) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
