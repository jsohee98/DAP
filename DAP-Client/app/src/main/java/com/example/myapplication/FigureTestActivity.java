package com.example.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FigureTestActivity extends AppCompatActivity {
    Button nextBtn;
    int count = 0;
    TextView questionTxt,pageCount;
    ImageView questionImg;
    @Override
    protected void onCreate(Bundle saveInt){
        super.onCreate(saveInt);
        setContentView(R.layout.first);

        nextBtn = findViewById(R.id.nextBtn);
        questionTxt = findViewById(R.id.firstText);
        questionImg = findViewById(R.id.figureTextImage);
        pageCount = findViewById(R.id.pageCount);
        pageCount.setText("1/3");
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 0){
                    count++;
                    pageCount.setText("2/3");
                    questionTxt.setText("2. 다음 도형 중 하나를 선택하여\n사각형의 안이나 바깥에\n크기나 위치에 관계없이\n 빨간색으로 세 번 그립니다.");
                    questionImg.setImageDrawable(getResources().getDrawable(R.drawable.figures));
                }else if(count == 1){
                    count++;
                    pageCount.setText("3/3");
                    questionImg.setVisibility(View.INVISIBLE);
                    questionTxt.setText("3. 선택하지 않은 나머지 도형을\n각각 한번씩 적당한 위치에\n파란색, 노란색, 초록색 순서로 \n자유롭게그립니다.");
                }else if(count == 2){
                    //사진 전송 페이지 이동
                    count = 0;
                    Intent intent = new Intent(getApplicationContext(), FigureTestPhotoActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
