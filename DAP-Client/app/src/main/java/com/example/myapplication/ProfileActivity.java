package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    public static final String NAMED_SP = "loginSetting";
    public static final String ACTIVITY_NAME = "setting";
    NetworkService networkService = APIClient.getNetworkService();
    public ListView listView;
    private TextView testCount,userIdField ;
    ListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sf = getSharedPreferences(NAMED_SP,MODE_PRIVATE);
        listView = findViewById(R.id.settingtestList);
        testCount = findViewById(R.id.testCount);
        userIdField = findViewById(R.id.userId);
        Log.e("userid", sf.getString("id",""));

        if(sf.getString("id","") != ""){
            userIdField.setText(sf.getString("id",""));
            Call<List<TestList>> test_list = networkService.callTestList(sf.getString("id",""));
            test_list.enqueue(new Callback<List<TestList>>() {

                @Override
                public void onResponse(Call<List<TestList>> call, Response<List<TestList>> response) {
                    List<TestList> testList = response.body();
                    final ListView listView;
                    testCount.setText("진단 수 : "+testList.size());
                    if(testList.size() != 0){
                        Log.e("Test","ok");
                        //Adapter 생성
                        adapter = new ListViewAdapter(ACTIVITY_NAME);
                        for(TestList test : testList){
                            adapter.addItem(test.testId, test.testDate, test.testCode);
                        }

                        //리스트뷰 참조 및 Adapter 달기
                        listView = findViewById(R.id.settingtestList);
                        listView.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<List<TestList>> call, Throwable t) {

                }
            });
        }
    }
}
