package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.customDialog.GetFeedbackDialog;

import java.io.File;
import java.util.Date;

public class TestResultActivity extends AppCompatActivity {
    TextView dateView, resultView, totalPoinView, resultHead, userSpokeResult, convertedResult;
    ImageView imageView;
    GetFeedbackDialog dialog;
    Button btn;
    String testType;
    boolean feedbackDialogOn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //feedback dialog on
        testType = getIntent().getStringExtra("testType");
        feedbackDialogOn = getIntent().getBooleanExtra("feedbackDialogOn",false);
        switch (testType){
            case "DT":
                setContentView(R.layout.activity_depression_test_result);
                depressionTestView(getIntent().getStringExtra("date"), getIntent().getIntExtra("total_point",0));
                break;
            case "WT":
                setContentView(R.layout.activity_word_test_result);
                wordsTestResultView(getIntent().getStringExtra("date"), getIntent().getStringExtra("userSpoke"), getIntent().getStringExtra("converted"));
                break;
            case "FT":
                setContentView(R.layout.activity_test_result);
                figureTestView(getIntent().getStringExtra("filePath"), getIntent().getStringExtra("date"), getIntent().getStringExtra("result"));
                break;
        }

        btn.setOnClickListener(v -> {
            if(feedbackDialogOn) {
                openFeedbackDialog(testType);
            }else{
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void figureTestView(String filePath, String nowdate, String result){
        resultView = findViewById(R.id.testResult);
        imageView = findViewById(R.id.testImageView);
        dateView = findViewById(R.id.testDate);
        btn = findViewById(R.id.homeBtn);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(filePath, options);
        imageView.setImageBitmap(originalBm);
        dateView.setText(nowdate);
        resultView.setText(result);
    }

    public void depressionTestView(String nowdate, int point){
        btn = findViewById(R.id.button);
        dateView = findViewById(R.id.dateView);
        totalPoinView = findViewById(R.id.totalPointView);
        resultHead = findViewById(R.id.resulthead);
        resultView = findViewById(R.id.resutlView);
        dateView.setText(nowdate);
        totalPoinView.setText("총 점수 "+point);
        if (point <16){
            resultHead.setText("편안한 상태입니다.");
            resultView.setText("지속적으로 정신건강에 관심을 갖고 예방을 위해 년1회 정기검사도 잊지 마세요.");
        }else if(point > 15 && point < 22 ){
            resultHead.setText("가벼운 경증에 가까운 우울 증상입니다.");
            resultView.setText("생활의 패턴의 변화로는 사람들을 많이 만날 수 있는 모임에 참가하거나, 여행, 독서 등 자신에게 여유와 생각할 기회를 제공할 수 있는 활동을 말합니다. 또한 우울증에 제일 좋은 것은 근육을 움직여서 활동성을 높일 수 있는 운동이 추천됩니다. 숨이 차오르면서 땀이 나는 운동을 말합니다.");
        }else if(point>21 && point <26 ){
            resultHead.setText("중간 정도의 우울증 증상입니다.");
            resultView.setText("위의 우울증 체크리스트는 간이 체크리스트이기 때문에 정확한 검사가 아니라고 생각을 하시면 됩니다.\n전문가의 도움을 받아 정확하게 우울증이 맞는지 확인을 받고 적절한 도움을 받아야 하는 정도입니다.");
        }else{
            resultHead.setText("현재 우울증상이 심각한 정도입니다.");
            resultView.setText("즉각적인 전문가의 개입이 필요하며, 왜 우울증이 나에게 왔는지 원인 파악과, 현재 우울증으로 힘든 곳을 해결할 수 있는 방법의 두 가지를 한 번에 접근하는 방법을 선택받게 됩니다.");
        }
    }
    public void wordsTestResultView(String date, String userSpoke, String converted) {
        btn = findViewById(R.id.returnBtn);
        dateView = findViewById(R.id.dateView);
        userSpokeResult = findViewById(R.id.userSpoke);
        convertedResult = findViewById(R.id.converted);

        dateView.setText(date);
        userSpokeResult.setText(userSpoke);
        convertedResult.setText(converted);
    }

    public void openFeedbackDialog(String testType){
        dialog = new GetFeedbackDialog(TestResultActivity.this, testType);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }
}
