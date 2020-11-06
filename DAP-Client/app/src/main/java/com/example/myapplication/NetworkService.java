package com.example.myapplication;

import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetworkService {

    @FormUrlEncoded
    @POST("api/list/add_test_result/")
    Call<TestList> saveDepressionTest(@Field("testId") String testId, @Field("userId") String userId, @Field("testCode") String testCode, @Field("testDate") String testDate, @Field("testPoint") int testPoint);

    @FormUrlEncoded
    @POST("api/list/add_test_result/")
    Call<TestList> saveWordTest(@Field("testId") String testId, @Field("userId") String userId, @Field("testCode") String TestCode, @Field("testDate") String testDate, @Field("textOrigin") String origin, @Field("testResult") String testResult);

    @Multipart
    @POST("api/list/add_test_result/")
    Call<FigureTest> saveFigureTest(@Part MultipartBody.Part file,
                                    @Part("testId") RequestBody testId,
                                    @Part("userId") RequestBody userId,
                                    @Part("testCode") RequestBody testCode,
                                    @Part("testDate") RequestBody testDate);

    @DELETE("api/list?")
    Call<List<TestList>> deleteTestResult(@Query("testId") String testId);

    @FormUrlEncoded
    @POST("api/user/")
    Call<Register> createRegister(@Field("userId") String userId, @Field("password") String password, @Field("email") String email,  @Field("gender") int gender, @Field("birth") String birth);

    @FormUrlEncoded
    @POST("api/applogin")
    Call<Login> loginApi(@Field("userId") String id, @Field("password") String pwd);

    @FormUrlEncoded
    @POST("api/usertestlist")
    Call<List<TestList>> callTestList(@Field("userId") String userId);

    @FormUrlEncoded
    @POST("api/get-dt-point")
    Call<DepressionTest> getDtPoint(@Field("testId") String testId);

    @FormUrlEncoded
    @POST("api/get-wt-result")
    Call<WordTest> getWtResult(@Field("testId") String testId);

    @FormUrlEncoded
    @POST("api/get-recommand-test")
    Call<RecommandTest> getRecommandTest(@Field("userId") String userId, @Field("emotion_point") int emotion_point);

    @GET("api/idcheck?")
    Call<Register> idcheck(@Query("userId") String userId);

    @GET("api/figure-shape-classification?")
    Call<FigureClassification> getPrimaryTemp(@Query("userId") String userId);

    @FormUrlEncoded
    @POST("api/add-recommand-data")
    Call<RecommandTest> addRecommandData(@Field("emotion_point") int emotionPoint, @Field("userId") String userId, @Field("feedback_point") int feedbackPoint, @Field("testCode") String testCode);

}
