package com.example.myapplication.customDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.myapplication.APIClient;
import com.example.myapplication.MainActivity;
import com.example.myapplication.NetworkService;
import com.example.myapplication.R;
import com.example.myapplication.RecommandTest;

import static android.content.Context.MODE_PRIVATE;

public class GetFeedbackDialog extends Dialog {
    public static final String RECOMMAND_SETTING = "recommandSetting";
    public static final String NAMED_SP = "loginSetting";
    public Button submitBtn;
    public String testCode;
    public RatingBar feedbackBar;
    public int feedbackPoint = 3;
    SharedPreferences rs,nsp;
    NetworkService networkService ;

    public GetFeedbackDialog(@NonNull Context context){
        super(context);
    }

    public GetFeedbackDialog(@NonNull Context context, String testType) {
        super(context);
        networkService = APIClient.getNetworkService();
        testCode = testType;
        rs = context.getSharedPreferences(RECOMMAND_SETTING, MODE_PRIVATE);
        nsp = context.getSharedPreferences(NAMED_SP,MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.dialog_feedback_layout);
        submitBtn = findViewById(R.id.btn_feedback);
        feedbackBar = findViewById(R.id.feedback_rating_indicator);
        Log.e("Test", "Check0");
        feedbackBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            feedbackPoint = (int)rating;
            Log.e("feedback point", feedbackPoint+"");
        });

        submitBtn.setOnClickListener(v -> {
            //emotion point, depression point, feedback point, test code save to database
            int emotionPoint = rs.getInt("emotion_point", 0);
            String userId = nsp.getString("id","");
            Log.e("add recommand data = ", userId+", "+emotionPoint+", "+feedbackPoint+", "+testCode);
            Call<RecommandTest> response = networkService.addRecommandData(emotionPoint, userId , feedbackPoint, testCode);
            response.enqueue(new Callback<RecommandTest>() {
                @Override
                public void onResponse(Call<RecommandTest> call, Response<RecommandTest> response) {
                    if(response.isSuccessful()){
                        Intent intent = new Intent(getContext(),MainActivity.class);
                        getContext().startActivity(intent);
                        GetFeedbackDialog.this.dismiss();
                    }
                    else{
                        Log.e("response is success, ", "fail");
                    }
                }

                @Override
                public void onFailure(Call<RecommandTest> call, Throwable t) {
                    Log.e("response is fail", t.getMessage());
                }
            });

        });
    }
}
