package com.example.mju.smile;

/**
 * Created by Jeong on 2017-12-01.
 */

public class Memory {

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
}
