package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepressionTestActivity extends AppCompatActivity {
    public static final String NAMED_SP = "loginSetting";
    public static final String RECOMMAND_SETTING = "recommandSetting";
    public ArrayList<String> questions = new ArrayList<>();
    public Button next, submit;
    public int count = 0,totalPoint = 0;
    public int[] point = new int[20];
    public RadioGroup rbGroup ;
    public TextView questionContainer, questionCount;
    public boolean rbCheck = false;
    public boolean feedbackDialogOn=false;
    SharedPreferences sp,rs;
    NetworkService networkService = APIClient.getNetworkService();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depression_test);

        sp = getSharedPreferences(NAMED_SP,MODE_PRIVATE);
        rs = getSharedPreferences(RECOMMAND_SETTING,MODE_PRIVATE);
        rbGroup = findViewById(R.id.rbGroup);
        next = findViewById(R.id.nextBtn);
        submit = findViewById(R.id.submitBtn);
        questionContainer = findViewById(R.id.questionContainer);
        questionCount = findViewById(R.id.questionCount);
        setQuestions();

        questionContainer.setText(questions.get(count).toString());
        rbGroup.setOnCheckedChangeListener((group, checkedId) -> {
            rbCheck = true;
            switch (checkedId){
                case R.id.button0:
                    point[count] = 0;
                    break;
                case R.id.button1:
                    point[count] = 1;
                    break;
                case R.id.button2:
                    point[count] = 2;
                    break;
                case R.id.button3:
                    point[count] = 3;
                    break;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbCheck){
                    if(count<20){
                        count ++;
                        int qCount = count + 1;
                        questionCount.setText(qCount +"/20");
                        rbGroup.clearCheck();
                        if(count == 19){
                            next.setClickable(false);
                            next.setEnabled(false);
                            next.setVisibility(View.INVISIBLE);
                            submit.setVisibility(View.VISIBLE);
                            submit.setEnabled(true);
                        }
                        rbCheck=false;
                        questionContainer.setText(questions.get(count).toString());
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"문항의 답을 골라주세요.",Toast.LENGTH_LONG).show();
                }

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<point.length;i++){
                    totalPoint +=point[i];
                }
                String userId = sp.getString("id","");
                SimpleDateFormat smd = new SimpleDateFormat("yyyy-MM-dd");
                final String nowDate = smd.format(new Date());
                Log.e("noew Date", nowDate);
                Random rand = new Random();
                int rn = rand.nextInt(10000000)+1;

                Call<TestList> response = networkService.saveDepressionTest("DT"+rn, userId, "DT", nowDate, totalPoint);
                response.enqueue(new Callback<TestList>() {
                    @Override
                    public void onResponse(Call<TestList> call, Response<TestList> response) {
                        if(response.isSuccessful()){
                            if(rs.getBoolean("recommandState",true)&&rs.getBoolean("recommandTest",false)){
                                feedbackDialogOn = true;
                            }else{
                                feedbackDialogOn = false;
                            }
                            Intent intent = new Intent(getApplicationContext(),TestResultActivity.class);
                            intent.putExtra("testType", "DT");
                            intent.putExtra("total_point", totalPoint);
                            intent.putExtra("date", nowDate+"");
                            intent.putExtra("feedbackDialogOn",feedbackDialogOn);
                            startActivity(intent);
                            finish();
                        }else{
                            Log.e("response is success", "fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<TestList> call, Throwable t) {
                        Log.e("on fail", t.getMessage().toString());
                    }
                });
                Log.e("Total Point", "point is "+totalPoint);
            }
        });


    }

    public void setQuestions(){
        questions.add("1) 아무렇지도 않던 일들이 괴롭고 귀찮게 느껴졌다.");
        questions.add("2) 먹고 싶지 않고 식욕이 없다.");
        questions.add("3) 어느 누가 도와준다 하더라도 나의 울적한 기분을 떨쳐 버릴 수 없을 것 같다.");
        questions.add("4) 무슨 일을 하던 정신을 집중하기가 힘들었다.");
        questions.add("5) 비교적 잘 지냈다.");
        questions.add("6) 상당히 우울했다.");
        questions.add("7) 모든 일들이 힘들게 느껴졌다.");
        questions.add("8) 앞일이 암담하게 느껴졌다.");
        questions.add("9) 지금까지의 내 인생은 실패작이라는 생각이 들었다.");
        questions.add("10) 적어도 보통 사람들만큼의 능력은 있었다고 생각한다.");
        questions.add("11) 잠을 설쳤다(잠을 잘 이루지 못했다).");
        questions.add("12) 두려움을 느꼈다.");
        questions.add("13) 평소에 비해 말수가 적었다.");
        questions.add("14) 세상에 홀로 있는 듯 한 외로움을 느꼈다.");
        questions.add("15) 큰 불만 없이 생활했다.");
        questions.add("16) 사람들이 나에게 차갑게 대하는 것 같았다.");
        questions.add("17) 갑자기 울음이 나왔다.");
        questions.add("18) 마음이 슬펐다.");
        questions.add("19) 사람들이 나를 싫어하는 것 같았다.");
        questions.add("20) 도무지 뭘 해 나갈 엄두가 나지 않았다.");
    }
}
