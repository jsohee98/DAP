package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingActivity extends AppCompatActivity {
    public static final String NAMED_SP = "loginSetting";
    protected void onCreate(Bundle savedInst){
        super.onCreate(savedInst);
        setContentView(R.layout.loading_layout);

        startLoading();
    }

    protected void startLoading(){
        SharedPreferences sf = getSharedPreferences(NAMED_SP, MODE_PRIVATE);
        if(sf.getBoolean("autoLogin",false)==true){
            NetworkService networkService = APIClient.getNetworkService();
            Call<Login> login = networkService.loginApi(sf.getString("id",""), sf.getString("pwd",""));
            login.enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    if(response.isSuccessful()){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {
                    Log.e("error", t.getMessage());
                }
            });
        }else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }
    }

}
