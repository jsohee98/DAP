package com.example.myapplication;

import android.media.Image;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class FigureTest {
    @SerializedName("userId")
    String userId;
    @SerializedName("testId")
    String testId;
    @SerializedName("testCode")
    String testCode;
    @SerializedName("testResult")
    String testResult;
    @SerializedName("testDate")
    String testDate;
    @SerializedName("testImage")
    File testImage;



    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
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

    public File getTestImage() {
        return testImage;
    }

    public void setTestImage(File testImage) {
        this.testImage = testImage;
    }
}
