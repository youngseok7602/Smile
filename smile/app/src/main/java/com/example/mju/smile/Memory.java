package com.example.mju.smile;

import java.io.Serializable;

/**
 * Created by Jeong on 2017-12-01.
 */

// 저장한 추억 또는 전달받은 추억 정보들에 대한 클래스
public class Memory implements Serializable {

    private String message_name;
    private String sender;
    private String latitude;
    private String longtitude;
    private String picture;
    private String dday;

    public Memory(){}

    public Memory(String message_name, String sender, String latitude, String longtitude,
        String picture, String dday){
        this.message_name = message_name;
        this.sender = sender;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.picture = picture;
        this.dday = dday;
    }

    public Memory(String str[]){
        this.message_name = str[0];
        this.sender = str[1];
        this.latitude = str[2];
        this.longtitude = str[3];
        this.picture = str[4];
        this.dday = str[5];
    }

    public String getMessage_name() {
        return message_name;
    }

    public String getSender() {
        return sender;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public String getPicture() {
        return picture;
    }

    public String getDday() {
        return dday;
    }

    public void setMessage_name(String message_name) {
        this.message_name = message_name;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setDday(String dday) {
        this.dday = dday;
    }
}
