package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RecommandTest {
    @SerializedName("testCode")
    public String testCode;

    @SerializedName("userId")
    public String userId;

    @SerializedName("emotion_point")
    public int emotion_point;

    @SerializedName("prediction")
    public ArrayList prediction;

    @SerializedName("depression_point")
    public int depression_point;

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getEmotion_point() {
        return emotion_point;
    }

    public void setEmotion_point(int emotion_point) {
        this.emotion_point = emotion_point;
    }

    public ArrayList getPrediction() {
        return prediction;
    }

    public void setPrediction(ArrayList prediction) {
        this.prediction = prediction;
    }

    public int getDepression_point() {
        return depression_point;
    }

    public void setDepression_point(int depressiont_point) {
        this.depression_point = depressiont_point;
    }
}
