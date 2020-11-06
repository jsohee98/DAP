package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Register {
    @SerializedName("userId")
    public String userId;
    @SerializedName("password")
    public String password;
    @SerializedName("email")
    public String email;
    @SerializedName("gender")
    public int gender;
    @SerializedName("birth")
    public String birth;
}
