package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class TestList {
    @SerializedName("testId")
    public String testId;
    @SerializedName("userId")
    public String userId;
    @SerializedName("testCode")
    public String testCode;
    @SerializedName("testDate")
    public String testDate;
    @SerializedName("testPoint")
    public int testPoint;

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public int getTestPoint() {
        return testPoint;
    }

    public void setTestPoint(int testPoint) {
        this.testPoint = testPoint;
    }
}