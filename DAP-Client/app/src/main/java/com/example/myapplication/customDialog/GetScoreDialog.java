package com.example.myapplication.customDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.APIClient;
import com.example.myapplication.DepressionTest;
import com.example.myapplication.DepressionTestActivity;
import com.example.myapplication.FigureTestActivity;
import com.example.myapplication.NetworkService;
import com.example.myapplication.R;
import com.example.myapplication.RecommandTest;
import com.example.myapplication.WordsActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class GetScoreDialog extends Dialog {
    public static final String NAMED_SP = "loginSetting";
    public static final String RECOMMAND_SETTING = "recommandSetting";
    private int feelingPoint = 2;
    private SeekBar pointBar ;
    private Button recomBtn, moreBtn, closeBtn;
    private TextView cardHead, cardTitle;
    private String userId;
    private LinearLayout testCard;
    private ImageButton notRecom;
    boolean recommandState = true;
    boolean recommandTest = false;
    SharedPreferences.Editor rs_editor;
    SharedPreferences sf, rs;
    NetworkService networkService = APIClient.getNetworkService();
    public GetScoreDialog(@NonNull Context context) {
        super(context);
        sf = context.getSharedPreferences(NAMED_SP, MODE_PRIVATE);
        rs = context.getSharedPreferences(RECOMMAND_SETTING,MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.dialog_recommand_system);
        pointBar = findViewById(R.id.mood_bar);
        recomBtn = findViewById(R.id.recommand_button);
        notRecom = findViewById(R.id.cancel_btn);
        closeBtn = findViewById(R.id.recommand_close_button);
        rs_editor = rs.edit();
//        sp_editor = sp.edit();
        pointBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                feelingPoint = progress;
                Log.e("feeling point",feelingPoint+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        notRecom.setOnClickListener(v -> {
            recommandState = false;
            rs_editor.putBoolean("recommandState",recommandState );
            rs_editor.commit();
            GetScoreDialog.this.dismiss();
        });

        recomBtn.setOnClickListener(v -> {
            userId = sf.getString("id","");
            Call<RecommandTest> response = networkService.getRecommandTest(userId,feelingPoint);
            response.enqueue(new Callback<RecommandTest>() {
                @Override
                public void onResponse(Call<RecommandTest> call, Response<RecommandTest> response) {
                    if(response.isSuccessful()){
                        RecommandTest recom = response.body();
                        ArrayList pred = recom.prediction;
                        String prediction = pred.get(0)+"";
                        rs_editor.putInt("emotion_point", feelingPoint);
                        rs_editor.commit();
                        changeDialog(prediction);
                    }else{
                        Log.e("onResponse", "false");
                    }
                }

                @Override
                public void onFailure(Call<RecommandTest> call, Throwable t) {
                    Log.e("onFailure", t.getMessage());
                }
            });
        });

        closeBtn.setOnClickListener(v -> {
            recommandTest = false;
            rs_editor.putBoolean("recommandTest",recommandTest);
            rs_editor.commit();
            GetScoreDialog.this.dismiss();
        });

    }

    private void changeDialog(String test){
        setContentView(R.layout.dialog_recommand_system_result);
        testCard = findViewById(R.id.test_card);
        cardHead = findViewById(R.id.card_head);
        cardTitle = findViewById(R.id.card_title);
        moreBtn = findViewById(R.id.recommand_button);
        switch (test){
            case "FT":
//              도형 심리 검사 추천
                cardHead.setText(R.string.figure_test_head);
                cardTitle.setText(R.string.figure_test_title);
                testCard.setOnTouchListener((v, event) -> {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        recommandTest = true;
                        rs_editor.putBoolean("recommandTest",recommandTest);
                        rs_editor.commit();
                        Intent intent = new Intent(getContext(), FigureTestActivity.class);
                        getContext().startActivity(intent);
                        GetScoreDialog.this.dismiss();
                    }
                    return false;
                });
                break;
            case "DT":
//              우울증 자가 진단
                cardHead.setText(R.string.depression_test_head);
                cardTitle.setText(R.string.depression_test_title);
                testCard.setOnTouchListener((v, event) -> {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        recommandTest = true;
                        rs_editor.putBoolean("recommandTest",recommandTest);
                        rs_editor.commit();
                        Intent intent = new Intent(getContext(), DepressionTestActivity.class);
                        getContext().startActivity(intent);
                        GetScoreDialog.this.dismiss();
                    }
                    return false;
                });
                break;
            case "WT":
//              단어 이용한 심리 검사
                cardHead.setText(R.string.word_test_head);
                cardTitle.setText(R.string.word_test_title);
                testCard.setOnTouchListener((v, event) -> {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        recommandTest = true;
                        rs_editor.putBoolean("recommandTest",recommandTest);
                        rs_editor.commit();
                        Intent intent = new Intent(getContext(), WordsActivity.class);
                        getContext().startActivity(intent);
                        GetScoreDialog.this.dismiss();
                    }
                    return false;
                });
                break;
            default:
                break;
        }

        moreBtn.setOnClickListener(v -> {
            recommandTest = false;
            rs_editor.putBoolean("recommandTest",recommandTest);
            rs_editor.commit();
            GetScoreDialog.this.dismiss();
        });
    }
}
